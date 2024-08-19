package com.github.rungeun.kcj.kotlincodejudge

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.PathManager
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.JBColor
import java.awt.Color
import java.io.File
import java.io.IOException
import javax.swing.*
import javax.swing.border.TitledBorder
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.fileEditor.FileEditorManagerEvent

class TestCaseRunner(
    private val projectBaseDir: String,
    private val project: Project,
    private val onExecutionFinished: () -> Unit,
    private val onTestCaseFinished: (Int, String) -> Unit
) {
    private var stopRequested = false
    private var currentFile: VirtualFile? = null

    init {
        // 파일 에디터 및 문서 변경 리스너 등록
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

    fun requestStop() {
        stopRequested = true
    }

    fun runAllTestCasesSequentially(testCasePanels: List<TestCaseComponents>) {
        stopRequested = false
        runTestCase(0, testCasePanels)
    }

    fun runSelectedTestCasesSequentially(selectedTestCasePanels: List<TestCaseComponents>) {
        stopRequested = false
        runTestCase(0, selectedTestCasePanels)
    }

    private fun runTestCase(index: Int, testCasePanels: List<TestCaseComponents>) {
        if (index >= testCasePanels.size || stopRequested) {
            onExecutionFinished()
            return
        }

        val testCase = testCasePanels[index]
        val input = testCase.inputTextArea.text
        val expectedOutput = testCase.outputTextArea.text

        object : SwingWorker<String, Void>() {
            private var startTime: Long = 0
            private var endTime: Long = 0

            override fun doInBackground(): String {
                SwingUtilities.invokeLater {
                    testCase.panel.border = BorderFactory.createTitledBorder("Judging...")
                }

                // 현재 파일 저장
                ApplicationManager.getApplication().invokeAndWait {
                    currentFile?.let { file ->
                        val document = FileDocumentManager.getInstance().getDocument(file)
                        document?.let {
                            FileDocumentManager.getInstance().saveDocument(it)
                        }
                    }
                }

                val compileResult = compileIfNeeded(testCase.answerTextArea, testCase.errorTextArea)

                if (compileResult == "SUCCESS") {
                    startTime = System.currentTimeMillis()
                    return runProgramWithCaching(input, expectedOutput, testCase.answerTextArea, testCase.errorTextArea)
                }
                return compileResult
            }

            override fun done() {
                if (stopRequested) {
                    SwingUtilities.invokeLater {
                        testCase.panel.border = BorderFactory.createTitledBorder("Stopped")
                    }
                    onExecutionFinished()
                    return
                }

                try {
                    val result = get()
                    endTime = System.currentTimeMillis()
                    val executionTime = endTime - startTime

                    SwingUtilities.invokeLater {
                        testCase.panel.border = when (result) {
                            "AC" -> BorderFactory.createTitledBorder(
                                BorderFactory.createLineBorder(JBColor.GREEN),
                                "AC - $executionTime ms",
                                TitledBorder.DEFAULT_JUSTIFICATION,
                                TitledBorder.DEFAULT_POSITION,
                                null,
                                JBColor.GREEN
                            )
                            "WA" -> BorderFactory.createTitledBorder(
                                BorderFactory.createLineBorder(Color.RED),
                                "WA - $executionTime ms",
                                TitledBorder.DEFAULT_JUSTIFICATION,
                                TitledBorder.DEFAULT_POSITION,
                                null,
                                Color.RED
                            )
                            "CE" -> BorderFactory.createTitledBorder(
                                BorderFactory.createLineBorder(Color.MAGENTA),
                                "CE - $executionTime ms",
                                TitledBorder.DEFAULT_JUSTIFICATION,
                                TitledBorder.DEFAULT_POSITION,
                                null,
                                Color.MAGENTA
                            )
                            "RE" -> BorderFactory.createTitledBorder(
                                BorderFactory.createLineBorder(Color.ORANGE),
                                "RE - $executionTime ms",
                                TitledBorder.DEFAULT_JUSTIFICATION,
                                TitledBorder.DEFAULT_POSITION,
                                null,
                                Color.ORANGE
                            )
                            "Stopped" -> BorderFactory.createTitledBorder(
                                BorderFactory.createLineBorder(Color.BLACK),
                                "Stopped",
                                TitledBorder.DEFAULT_JUSTIFICATION,
                                TitledBorder.DEFAULT_POSITION,
                                null,
                                Color.BLACK
                            )
                            else -> BorderFactory.createTitledBorder(
                                BorderFactory.createLineBorder(JBColor.GRAY),
                                "Unknown Error",
                                TitledBorder.DEFAULT_JUSTIFICATION,
                                TitledBorder.DEFAULT_POSITION,
                                null,
                                JBColor.GRAY
                            )
                        }

                        // UI 상태 관리 (AC인 경우 Folded, 다른 경우 Expanded)
                        when (result) {
                            "AC" -> testCase.uiStateManager.setState(UIState.UiFolded)
                            else -> testCase.uiStateManager.setState(UIState.UiExpanded)
                        }
                    }

                    SwingUtilities.invokeLater {
                        onTestCaseFinished(index, result)
                    }

                } catch (e: Exception) {
                    SwingUtilities.invokeLater {
                        testCase.panel.border = BorderFactory.createTitledBorder(
                            BorderFactory.createLineBorder(Color.BLACK),
                            "Error",
                            TitledBorder.DEFAULT_JUSTIFICATION,
                            TitledBorder.DEFAULT_POSITION,
                            null,
                            Color.BLACK
                        )
                        testCase.errorTextArea.text = "Error during execution: ${e.message}"
                        testCase.uiStateManager.setState(UIState.UiExpanded)
                    }
                } finally {
                    runTestCase(index + 1, testCasePanels)
                }
            }

        }.execute()
    }

    private fun compileIfNeeded(
        answerTextArea: JTextArea,
        errorTextArea: JTextArea
    ): String {
        val virtualFile = currentFile ?: return "No file open."

        val buildDir = File("$projectBaseDir/buildtc")
        if (!buildDir.exists()) {
            buildDir.mkdirs()
        }

        val jarFile = File(buildDir, "TempProgram.jar")

        return try {
            val ideaHome = PathManager.getHomePath() ?: throw IOException("IntelliJ IDEA installation path not found.")
            val kotlincPath = "$ideaHome/plugins/Kotlin/kotlinc/bin/kotlinc.bat"

            if (!jarFile.exists() || jarFile.lastModified() < virtualFile.timeStamp) {
                val compileProcess = ProcessBuilder(kotlincPath, virtualFile.path, "-include-runtime", "-d", jarFile.absolutePath)
                    .directory(buildDir)
                    .redirectErrorStream(true)
                    .start()
                compileProcess.waitFor()

                if (compileProcess.exitValue() != 0) {
                    val errorOutput = compileProcess.inputStream.bufferedReader().readText()
                    errorTextArea.text = "Compilation Errors:\n$errorOutput"
                    return "CE"
                }
                return "SUCCESS"
            }

            return "SUCCESS"
        } catch (e: IOException) {
            errorTextArea.text = "Error during execution: ${e.message}"
            return "CE"
        } catch (e: Exception) {
            errorTextArea.text = "Runtime Error: ${e.message}"
            return "RE"
        }
    }

    private fun runProgramWithCaching(
        input: String,
        expectedOutput: String,
        answerTextArea: JTextArea,
        errorTextArea: JTextArea
    ): String {
        val buildDir = File("$projectBaseDir/buildtc")
        val jarFile = File(buildDir, "TempProgram.jar")

        val runProcess = ProcessBuilder("java", "-jar", jarFile.absolutePath)
            .directory(jarFile.parentFile)
            .redirectInput(ProcessBuilder.Redirect.PIPE)
            .redirectOutput(File(jarFile.parentFile, "TempProgramOutput.txt"))
            .redirectError(File(jarFile.parentFile, "TempProgramErr.txt"))
            .start()

        runProcess.outputStream.bufferedWriter().use { writer ->
            writer.write(input)
        }
        runProcess.waitFor()

        val errOutput = File(jarFile.parentFile, "TempProgramErr.txt").readText()
        errorTextArea.text = errOutput

        val actualOutput = File(jarFile.parentFile, "TempProgramOutput.txt").readText()
        answerTextArea.text = actualOutput

        val isRuntimeError = errOutput.isNotBlank() && errOutput.contains("Exception")
        val isCorrect = compareOutputs(actualOutput, expectedOutput)

        return when {
            isRuntimeError -> "RE"
            isCorrect -> "AC"
            else -> "WA"
        }
    }

    private fun compareOutputs(actualOutput: String, expectedOutput: String): Boolean {
        val normalizedActualOutput = actualOutput.lines().map { it.trimEnd() }.filter { it.isNotEmpty() }.joinToString("\n")
        val normalizedExpectedOutput = expectedOutput.lines().map { it.trimEnd() }.filter { it.isNotEmpty() }.joinToString("\n")
        return normalizedActualOutput == normalizedExpectedOutput
    }
}
