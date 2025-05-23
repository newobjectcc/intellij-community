// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.openapi.fileEditor

import com.intellij.codeWithMe.ClientId
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.ex.FileEditorWithProvider
import com.intellij.openapi.fileEditor.impl.EditorComposite
import com.intellij.openapi.fileEditor.impl.EditorCompositeModel
import com.intellij.openapi.fileEditor.impl.FileEditorOpenOptions
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VirtualFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import org.jetbrains.annotations.ApiStatus
import org.jetbrains.annotations.ApiStatus.Experimental

/**
 * Per-client version of file editor manager
 */
@ApiStatus.Internal
@Experimental
interface ClientFileEditorManager {
  companion object {
    @JvmStatic
    fun getCurrentInstance(project: Project): ClientFileEditorManager = project.service()

    @JvmStatic
    fun getClientId(fileEditor: FileEditor): ClientId? = CLIENT_ID.get(fileEditor)

    @JvmStatic
    @ApiStatus.Internal
    fun assignClientId(fileEditor: FileEditor, clientId: ClientId?): Unit = CLIENT_ID.set(fileEditor, clientId)

    private val CLIENT_ID = Key.create<ClientId>("CLIENT_ID")
  }

  fun getSelectedEditorWithProvider(file: VirtualFile): FileEditorWithProvider?
  fun getSelectedEditor(): FileEditor?
  fun getSelectedEditorWithProvider(): FileEditorWithProvider?
  fun getSelectedEditorWithProviderFlow(): Flow<FileEditorWithProvider?>
  fun getSelectedEditors(): List<FileEditor>
  fun getSelectedTextEditor(): Editor?
  fun getSelectedFile(): VirtualFile?
  fun getSelectedFiles(): List<VirtualFile>

  fun setSelectedEditor(file: VirtualFile, providerId: String)

  fun getEditorsWithProviders(file: VirtualFile): List<FileEditorWithProvider>
  fun getEditors(file: VirtualFile): List<FileEditor>

  fun openFile(file: VirtualFile, options: FileEditorOpenOptions): FileEditorComposite

  @Experimental
  suspend fun openFileAsync(file: VirtualFile, options: FileEditorOpenOptions): FileEditorComposite

  fun closeFile(file: VirtualFile, closeAllCopies: Boolean)
  fun isFileOpen(file: VirtualFile): Boolean

  fun createComposite(
    file: VirtualFile,
    coroutineScope: CoroutineScope,
    model: Flow<EditorCompositeModel>,
  ): EditorComposite?

  fun getComposite(file: VirtualFile): EditorComposite?
  fun getComposite(editor: FileEditor): EditorComposite?
  fun getAllComposites(file: VirtualFile): List<EditorComposite>
  fun getAllComposites(): List<EditorComposite>
  fun removeComposite(composite: EditorComposite)

  fun getAllFiles(): List<VirtualFile>
  fun getAllEditors(): List<FileEditor>
}