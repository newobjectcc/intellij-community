// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.

package org.jetbrains.kotlin.idea.refactoring.move

import com.google.gson.JsonObject
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.*
import com.intellij.refactoring.MoveDestination
import com.intellij.refactoring.PackageWrapper
import com.intellij.refactoring.move.MoveHandler
import com.intellij.refactoring.move.moveClassesOrPackages.*
import com.intellij.refactoring.move.moveFilesOrDirectories.MoveFilesOrDirectoriesProcessor
import com.intellij.refactoring.move.moveInner.MoveInnerProcessor
import com.intellij.refactoring.move.moveMembers.MockMoveMembersOptions
import com.intellij.refactoring.move.moveMembers.MoveMembersProcessor
import org.jetbrains.kotlin.idea.base.util.allScope
import org.jetbrains.kotlin.idea.base.util.projectScope
import org.jetbrains.kotlin.idea.core.util.toPsiDirectory
import org.jetbrains.kotlin.idea.core.util.toPsiFile
import org.jetbrains.kotlin.idea.jsonUtils.getNullableString
import org.jetbrains.kotlin.idea.base.util.getString
import org.jetbrains.kotlin.idea.core.createKotlinFile
import org.jetbrains.kotlin.idea.refactoring.AbstractMultifileRefactoringTest
import org.jetbrains.kotlin.idea.refactoring.KotlinRefactoringSettings
import org.jetbrains.kotlin.idea.refactoring.move.changePackage.KotlinChangePackageRefactoring
import org.jetbrains.kotlin.idea.refactoring.move.moveClassesOrPackages.KotlinAwareDelegatingMoveDestination
import org.jetbrains.kotlin.idea.refactoring.move.moveDeclarations.MoveKotlinDeclarationsProcessor
import org.jetbrains.kotlin.idea.refactoring.move.moveMethod.MoveKotlinMethodProcessor
import org.jetbrains.kotlin.idea.refactoring.runRefactoringTest
import org.jetbrains.kotlin.idea.stubindex.KotlinFullClassNameIndex
import org.jetbrains.kotlin.idea.stubindex.KotlinFunctionShortNameIndex
import org.jetbrains.kotlin.idea.stubindex.KotlinPropertyShortNameIndex
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject
import org.jetbrains.kotlin.psi.psiUtil.getNonStrictParentOfType

abstract class AbstractMoveTest : AbstractMultifileRefactoringTest() {
    override fun isEnabled(config: JsonObject): Boolean = config.getString("enabledInK1").toBooleanStrict()

    override fun runRefactoring(path: String, config: JsonObject, rootDir: VirtualFile, project: Project) {
        runMoveRefactoring(path, config, rootDir, project)
    }
}

fun runMoveRefactoring(path: String, config: JsonObject, rootDir: VirtualFile, project: Project) {
    runRefactoringTest(path, config, rootDir, project, MoveAction.valueOf(config.getString("type")))
}

enum class MoveAction : AbstractMultifileRefactoringTest.RefactoringAction {
    MOVE_MEMBERS {
        override fun runRefactoring(rootDir: VirtualFile, mainFile: PsiFile, elementsAtCaret: List<PsiElement>, config: JsonObject) {
            val members = elementsAtCaret.map { it.getNonStrictParentOfType<PsiMember>()!! }
            val targetClassName = config.getString("targetClass")
            val visibility = config.getNullableString("visibility")

            val options = MockMoveMembersOptions(targetClassName, members.toTypedArray())
            if (visibility != null) {
                options.memberVisibility = visibility
            }

            MoveMembersProcessor(mainFile.project, options).run()
        }
    },

    MOVE_TOP_LEVEL_CLASSES {
        override fun runRefactoring(rootDir: VirtualFile, mainFile: PsiFile, elementsAtCaret: List<PsiElement>, config: JsonObject) {
            val classesToMove = elementsAtCaret.map { it.getNonStrictParentOfType<PsiClass>()!! }
            val targetPackage = config.getString("targetPackage")

            MoveClassesOrPackagesProcessor(
                mainFile.project,
                classesToMove.toTypedArray(),
                MultipleRootsMoveDestination(PackageWrapper(mainFile.manager, targetPackage)),
                /* searchInComments = */ false,
                /* searchInNonJavaFiles = */ true,
                /* moveCallback = */ null
            ).run()
        }
    },

    MOVE_PACKAGES {
        override fun runRefactoring(rootDir: VirtualFile, mainFile: PsiFile, elementsAtCaret: List<PsiElement>, config: JsonObject) {
            val project = mainFile.project
            val sourcePackageName = config.getString("sourcePackage")
            val targetPackageName = config.getString("targetPackage")

            val sourcePackage = JavaPsiFacade.getInstance(project).findPackage(sourcePackageName)!!
            val targetPackage = JavaPsiFacade.getInstance(project).findPackage(targetPackageName)
            val targetDirectory = targetPackage?.directories?.first()

            val targetPackageWrapper = PackageWrapper(mainFile.manager, targetPackageName)
            val moveDestination = if (targetDirectory != null) {
                val targetSourceRoot = ProjectRootManager.getInstance(project).fileIndex.getSourceRootForFile(targetDirectory.virtualFile)!!
                KotlinAwareDelegatingMoveDestination(
                    AutocreatingSingleSourceRootMoveDestination(targetPackageWrapper, targetSourceRoot),
                    targetPackage,
                    targetDirectory
                )
            } else {
                MultipleRootsMoveDestination(targetPackageWrapper)
            }

            MoveClassesOrPackagesProcessor(
                project,
                arrayOf(sourcePackage),
                moveDestination,
                /* searchInComments = */ false,
                /* searchInNonJavaFiles = */ true,
                /* moveCallback = */ null
            ).run()
        }
    },

    MOVE_TOP_LEVEL_CLASSES_TO_INNER {
        override fun runRefactoring(rootDir: VirtualFile, mainFile: PsiFile, elementsAtCaret: List<PsiElement>, config: JsonObject) {
            val project = mainFile.project

            val classesToMove = elementsAtCaret.map { it.getNonStrictParentOfType<PsiClass>()!! }
            val targetClass = config.getString("targetClass")

            MoveClassToInnerProcessor(
                project,
                classesToMove.toTypedArray(),
                JavaPsiFacade.getInstance(project).findClass(targetClass, project.allScope())!!,
                /* searchInComments = */ false,
                /* searchInNonJavaFiles = */ true,
                /* moveCallback = */ null
            ).run()
        }
    },

    MOVE_INNER_CLASS {
        override fun runRefactoring(rootDir: VirtualFile, mainFile: PsiFile, elementsAtCaret: List<PsiElement>, config: JsonObject) {
            val project = mainFile.project

            val classToMove = elementsAtCaret.single().getNonStrictParentOfType<PsiClass>()!!
            val newClassName = config.getNullableString("newClassName") ?: classToMove.name!!
            val outerInstanceParameterName = config.getNullableString("outerInstanceParameterName")
            val targetPackage = config.getString("targetPackage")

            MoveInnerProcessor(
                project,
                classToMove,
                newClassName,
                outerInstanceParameterName != null,
                outerInstanceParameterName,
                JavaPsiFacade.getInstance(project).findPackage(targetPackage)!!.directories[0]
            ).run()
        }
    },

    MOVE_FILES {
        override fun runRefactoring(rootDir: VirtualFile, mainFile: PsiFile, elementsAtCaret: List<PsiElement>, config: JsonObject) {
            val project = mainFile.project

            val targetPackage = config.getNullableString("targetPackage")
            val targetDirPath = targetPackage?.replace('.', '/') ?: config.getNullableString("targetDirectory")
            if (targetDirPath != null) {
                runWriteAction { VfsUtil.createDirectoryIfMissing(rootDir, targetDirPath) }
                val newParent = if (targetPackage != null) {
                    JavaPsiFacade.getInstance(project).findPackage(targetPackage)!!.directories[0]
                } else {
                    rootDir.findFileByRelativePath(targetDirPath)!!.toPsiDirectory(project)!!
                }
                MoveFilesOrDirectoriesProcessor(
                    project,
                    arrayOf(mainFile),
                    newParent,
                    /* searchInComments = */ false,
                    /* searchInNonJavaFiles = */ true,
                    /* moveCallback = */ null,
                    /* prepareSuccessfulCallback = */ null
                ).run()
            } else {
                val targetFile = config.getString("targetFile")

                MoveHandler.doMove(
                    project,
                    arrayOf(mainFile),
                    PsiManager.getInstance(project).findFile(rootDir.findFileByRelativePath(targetFile)!!)!!,
                    /* dataContext = */ null,
                    /* callback = */ null
                )
            }
        }
    },

    MOVE_FILES_WITH_DECLARATIONS {
        override fun runRefactoring(rootDir: VirtualFile, mainFile: PsiFile, elementsAtCaret: List<PsiElement>, config: JsonObject) {
            val project = mainFile.project
            val elementsToMove = config.getAsJsonArray("filesToMove").map {
                val virtualFile = rootDir.findFileByRelativePath(it.asString)!!
                if (virtualFile.isDirectory) virtualFile.toPsiDirectory(project)!! else virtualFile.toPsiFile(project)!!
            }
            val targetDirPath = config.getString("targetDirectory")
            val targetDir = rootDir.findFileByRelativePath(targetDirPath)!!.toPsiDirectory(project)!!
            KotlinAwareMoveFilesOrDirectoriesProcessor(
                project,
                elementsToMove,
                targetDir,
                true,
                searchInComments = true,
                searchInNonJavaFiles = true,
                moveCallback = null
            ).run()
        }
    },

    MOVE_KOTLIN_TOP_LEVEL_DECLARATIONS {
        override fun runRefactoring(rootDir: VirtualFile, mainFile: PsiFile, elementsAtCaret: List<PsiElement>, config: JsonObject) {
            val project = mainFile.project
            val elementsToMove = elementsAtCaret.map { it.getNonStrictParentOfType<KtNamedDeclaration>()!! }

            val moveTarget = config.getNullableString("targetPackage")?.let { packageName ->
                val targetSourceRootPath = config["targetSourceRoot"]?.asString
                val packageWrapper = PackageWrapper(mainFile.manager, packageName)
                val moveDestination: MoveDestination = targetSourceRootPath?.let {
                    AutocreatingSingleSourceRootMoveDestination(packageWrapper, rootDir.findFileByRelativePath(it)!!)
                } ?: MultipleRootsMoveDestination(packageWrapper)
                val destDirIfAny = moveDestination.getTargetIfExists(mainFile)
                val targetDir = if (targetSourceRootPath != null) {
                    rootDir.findFileByRelativePath(targetSourceRootPath)!!
                } else {
                    destDirIfAny?.virtualFile
                }
                KotlinMoveTarget.DeferredFile(FqName(packageName), targetDir) {
                    createKotlinFile(guessNewFileName(elementsToMove)!!, moveDestination.getTargetDirectory(mainFile))
                }
            } ?: config.getString("targetFile").let { filePath ->
                KotlinMoveTarget.ExistingElement(
                    PsiManager.getInstance(project).findFile(rootDir.findFileByRelativePath(filePath)!!) as KtFile
                )
            }

            val descriptor = MoveDeclarationsDescriptor(
                project,
                KotlinMoveSource(elementsToMove),
                moveTarget,
                KotlinMoveDeclarationDelegate.TopLevel,
                deleteSourceFiles = true
            )
            MoveKotlinDeclarationsProcessor(descriptor).run()
        }
    },

    CHANGE_PACKAGE_DIRECTIVE {
        override fun runRefactoring(rootDir: VirtualFile, mainFile: PsiFile, elementsAtCaret: List<PsiElement>, config: JsonObject) {
            withCorrectMoveSettings(config) {
                KotlinChangePackageRefactoring(mainFile as KtFile).run(FqName(config.getString("newPackageName")))
            }
        }

        private fun withCorrectMoveSettings(config: JsonObject, action: () -> Unit) {
            val kotlinSettings = KotlinRefactoringSettings.instance
            val snapshot = KotlinRefactoringSettings().apply { loadState(kotlinSettings) }

            try {
                kotlinSettings.apply {
                    MOVE_SEARCH_IN_COMMENTS = config.get("searchInCommentsAndStrings")?.asBoolean ?: MOVE_SEARCH_IN_COMMENTS
                    MOVE_SEARCH_FOR_TEXT = config.get("searchInNonCode")?.asBoolean ?: MOVE_SEARCH_FOR_TEXT
                }

                action()
            } finally {
                kotlinSettings.loadState(snapshot)
            }
        }
    },

    MOVE_DIRECTORY_WITH_CLASSES {
        override fun runRefactoring(rootDir: VirtualFile, mainFile: PsiFile, elementsAtCaret: List<PsiElement>, config: JsonObject) {
            val project = mainFile.project
            val sourceDir = rootDir.findFileByRelativePath(config.getString("sourceDir"))!!.toPsiDirectory(project)!!
            val targetDir = rootDir.findFileByRelativePath(config.getString("targetDir"))!!.toPsiDirectory(project)!!
            MoveDirectoryWithClassesProcessor(project, arrayOf(sourceDir), targetDir, true, true, true) {}.run()
        }
    },

    MOVE_KOTLIN_NESTED_CLASS {
        override fun runRefactoring(rootDir: VirtualFile, mainFile: PsiFile, elementsAtCaret: List<PsiElement>, config: JsonObject) {
            val project = mainFile.project
            val elementToMove = elementsAtCaret.single().getNonStrictParentOfType<KtClassOrObject>()!!
            val targetClassName = config.getNullableString("targetClass")
            val targetClass =
                if (targetClassName != null) {
                    KotlinFullClassNameIndex.get(targetClassName, project, project.projectScope()).first()!!
                } else null
            val delegate = KotlinMoveDeclarationDelegate.NestedClass(
                config.getNullableString("newName"),
                config.getNullableString("outerInstanceParameter")
            )
            val moveTarget =
                if (targetClass != null) {
                    KotlinMoveTarget.ExistingElement(targetClass)
                } else {
                    val fileName = (delegate.newClassName ?: elementToMove.name!!) + ".kt"
                    val targetPackageFqName = (mainFile as KtFile).packageFqName
                    val targetDir = mainFile.containingDirectory!!
                    KotlinMoveTarget.DeferredFile(targetPackageFqName, targetDir.virtualFile) {
                        createKotlinFile(fileName, targetDir, targetPackageFqName.asString())
                    }
                }
            val descriptor = MoveDeclarationsDescriptor(project, KotlinMoveSource(elementToMove), moveTarget, delegate)
            MoveKotlinDeclarationsProcessor(descriptor).run()
        }
    },

    MOVE_KOTLIN_METHOD {
        override fun runRefactoring(rootDir: VirtualFile, mainFile: PsiFile, elementsAtCaret: List<PsiElement>, config: JsonObject) {
            val project = mainFile.project
            val method =
                KotlinFunctionShortNameIndex.get(config.getString("methodToMove"), project, project.projectScope()).first()
            val methodParameterName = config.getNullableString("methodParameter")
            val sourcePropertyName = config.getNullableString("sourceProperty")
            val targetObjectName = config.getNullableString("targetObject")
            val targetVariable = when {
                methodParameterName != null -> method.valueParameters.find { it.name == methodParameterName }!!
                sourcePropertyName != null -> KotlinPropertyShortNameIndex.get(sourcePropertyName, project, project.projectScope()).first()
                else -> KotlinFullClassNameIndex.get(targetObjectName!!, project, project.projectScope()).first()

            }
            val oldClassParameterNames = mutableMapOf<KtClass, String>()
            val outerInstanceParameter = config.getNullableString("outerInstanceParameter")
            if (outerInstanceParameter != null) {
                oldClassParameterNames[method.containingClassOrObject as KtClass] = outerInstanceParameter
            }
            MoveKotlinMethodProcessor(method, targetVariable, oldClassParameterNames).run()
        }
    };
}
