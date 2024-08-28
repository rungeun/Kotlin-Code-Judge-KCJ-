package com.github.rungeun.kcj.kotlincodejudge.controller

import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.fileEditor.FileEditorManagerEvent

class FileTracker(private val project: Project) {

    private var currentFile: VirtualFile? = null

    init {
        val connection = project.messageBus.connect()
        connection.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, MyFileEditorManagerListener())
        EditorFactory.getInstance().eventMulticaster.addDocumentListener(MyDocumentListener(), connection)
        updateCurrentFile()
    }

    private inner class MyFileEditorManagerListener : FileEditorManagerListener {
        override fun selectionChanged(event: FileEditorManagerEvent) {
            updateCurrentFile()
        }
    }

    private inner class MyDocumentListener : DocumentListener {
        override fun documentChanged(event: DocumentEvent) {
            updateCurrentFile()
        }
    }

    private fun updateCurrentFile() {
        val editor = FileEditorManager.getInstance(project).selectedTextEditor
        if (editor != null) {
            currentFile = editor.virtualFile
        } else {
            currentFile = null
        }
    }

    fun getCurrentFile(): VirtualFile? {
        return currentFile
    }
}
