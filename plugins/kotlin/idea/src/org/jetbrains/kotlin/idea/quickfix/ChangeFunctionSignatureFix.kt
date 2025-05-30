// Copyright 2000-2025 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.

package org.jetbrains.kotlin.idea.quickfix

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.builtins.StandardNames
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor.Kind.SYNTHESIZED
import org.jetbrains.kotlin.descriptors.ConstructorDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.diagnostics.DiagnosticFactory
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.idea.base.codeInsight.KotlinNameSuggester
import org.jetbrains.kotlin.idea.base.fe10.codeInsight.newDeclaration.Fe10KotlinNameSuggester
import org.jetbrains.kotlin.idea.base.resources.KotlinBundle
import org.jetbrains.kotlin.idea.caches.resolve.analyze
import org.jetbrains.kotlin.idea.codeInsight.DescriptorToSourceUtilsIde
import org.jetbrains.kotlin.idea.codeinsight.api.classic.quickfixes.KotlinQuickFixAction
import org.jetbrains.kotlin.idea.core.mapArgumentsToParameters
import org.jetbrains.kotlin.idea.refactoring.canRefactor
import org.jetbrains.kotlin.idea.refactoring.changeSignature.KotlinChangeSignatureConfiguration
import org.jetbrains.kotlin.idea.refactoring.changeSignature.KotlinMethodDescriptor
import org.jetbrains.kotlin.idea.refactoring.changeSignature.modify
import org.jetbrains.kotlin.idea.refactoring.changeSignature.runChangeSignature
import org.jetbrains.kotlin.idea.util.getDataFlowAwareTypes
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType
import org.jetbrains.kotlin.resolve.calls.util.getCall
import org.jetbrains.kotlin.resolve.calls.util.getType
import org.jetbrains.kotlin.resolve.isValueClass
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode
import org.jetbrains.kotlin.types.checker.KotlinTypeChecker

abstract class ChangeFunctionSignatureFix(
    element: PsiElement,
    protected val functionDescriptor: FunctionDescriptor
) : KotlinQuickFixAction<PsiElement>(element) {
    override fun getFamilyName() = FAMILY_NAME

    override fun startInWriteAction() = false

    override fun isAvailable(project: Project, editor: Editor?, file: KtFile): Boolean {
        val declarations = DescriptorToSourceUtilsIde.getAllDeclarations(project, functionDescriptor)
        return declarations.isNotEmpty() && declarations.all { it.isValid && it.canRefactor() }
    }

    protected fun getNewArgumentName(argument: ValueArgument, validator: Function1<String, Boolean>): String {
        val expression = KtPsiUtil.deparenthesize(argument.getArgumentExpression())
        val argumentName = argument.getArgumentName()?.asName?.asString()
            ?: (expression as? KtNameReferenceExpression)?.getReferencedName()?.takeIf { !isSpecialName(it) }

        return when {
            argumentName != null -> KotlinNameSuggester.suggestNameByName(argumentName, validator)
            expression != null -> {
                val bindingContext = expression.analyze(BodyResolveMode.PARTIAL)
                val expressionText = expression.text
                if (isSpecialName(expressionText)) {
                    val type = expression.getType(bindingContext)
                    if (type != null) {
                        return Fe10KotlinNameSuggester.suggestNamesByType(type, validator, "param").first()
                    }
                }
                Fe10KotlinNameSuggester.suggestNamesByExpressionAndType(expression, null, bindingContext, validator, "param").first()
            }
            else -> KotlinNameSuggester.suggestNameByName("param", validator)
        }
    }

    private fun isSpecialName(name: String): Boolean {
        return name == StandardNames.IMPLICIT_LAMBDA_PARAMETER_NAME.identifier || name == "field"
    }

    companion object : KotlinSingleIntentionActionFactoryWithDelegate<KtCallElement, CallableDescriptor>() {
        override fun getElementOfInterest(diagnostic: Diagnostic): KtCallElement? = diagnostic.psiElement.getStrictParentOfType()

        override fun extractFixData(element: KtCallElement, diagnostic: Diagnostic): CallableDescriptor {
            return DiagnosticFactory.cast(diagnostic, Errors.TOO_MANY_ARGUMENTS, Errors.NO_VALUE_FOR_PARAMETER).a
        }

        override fun createFix(originalElement: KtCallElement, data: CallableDescriptor): ChangeFunctionSignatureFix? {
            val functionDescriptor = data as? FunctionDescriptor
                ?: (data as? ValueParameterDescriptor)?.containingDeclaration as? FunctionDescriptor
                ?: return null

            if (functionDescriptor.kind == SYNTHESIZED) return null

            if (data is ValueParameterDescriptor) {
                return RemoveParameterFix(originalElement, functionDescriptor, data)
            } else {
                val parameters = functionDescriptor.valueParameters
                val arguments = originalElement.valueArguments

                if (arguments.size > parameters.size) {
                    if (functionDescriptor is ConstructorDescriptor &&
                        functionDescriptor.containingDeclaration.isValueClass()
                    ) return null

                    val bindingContext = originalElement.analyze()
                    val call = originalElement.getCall(bindingContext) ?: return null
                    val argumentToParameter = call.mapArgumentsToParameters(functionDescriptor)
                    val hasTypeMismatches = argumentToParameter.any { (argument, parameter) ->
                        val argumentTypes = argument.getArgumentExpression()?.let {
                            getDataFlowAwareTypes(
                                it,
                                bindingContext
                            )
                        }
                        argumentTypes?.none { dataFlowAwareType ->
                            KotlinTypeChecker.DEFAULT.isSubtypeOf(dataFlowAwareType, parameter.type)
                        } ?: true
                    }
                    val kind = when {
                        hasTypeMismatches -> AddFunctionParametersFix.Kind.ChangeSignature
                        else -> AddFunctionParametersFix.Kind.AddParameterGeneric
                    }
                    return AddFunctionParametersFix(originalElement, functionDescriptor, kind)
                }
            }

            return null
        }

        private class RemoveParameterFix(
            element: PsiElement,
            functionDescriptor: FunctionDescriptor,
            private val parameterToRemove: ValueParameterDescriptor
        ) : ChangeFunctionSignatureFix(element, functionDescriptor) {
            override fun getText() = KotlinBundle.message("fix.change.signature.remove.parameter", parameterToRemove.name.asString())

            override fun invoke(project: Project, editor: Editor?, file: KtFile) {
                runRemoveParameter(parameterToRemove, element ?: return, editor)
            }
        }

        val FAMILY_NAME = KotlinBundle.message("fix.change.signature.family")

        fun runRemoveParameter(parameterDescriptor: ValueParameterDescriptor, context: PsiElement, editor: Editor?) {
            val functionDescriptor = parameterDescriptor.containingDeclaration as FunctionDescriptor
            runChangeSignature(
                context.project,
                editor,
                functionDescriptor,
                object : KotlinChangeSignatureConfiguration {
                    override fun configure(originalDescriptor: KotlinMethodDescriptor): KotlinMethodDescriptor {
                        return originalDescriptor.modify { descriptor ->
                            val index = if (descriptor.receiver != null) parameterDescriptor.index + 1 else parameterDescriptor.index
                            descriptor.removeParameter(index)
                        }
                    }

                    override fun isPerformSilently(affectedFunctions: Collection<PsiElement>) = true
                    override fun isForcePerformForSelectedFunctionOnly() = false
                },
                context,
                KotlinBundle.message("fix.change.signature.remove.parameter.command", parameterDescriptor.name.asString())
            )
        }

    }
}


