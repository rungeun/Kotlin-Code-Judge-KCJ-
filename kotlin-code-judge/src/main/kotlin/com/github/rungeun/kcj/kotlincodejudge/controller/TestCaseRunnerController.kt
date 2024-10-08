package com.github.rungeun.kcj.kotlincodejudge.controller

import com.github.rungeun.kcj.kotlincodejudge.TestCaseComponents
import com.github.rungeun.kcj.kotlincodejudge.UIState
import com.github.rungeun.kcj.kotlincodejudge.model.TestCaseModel
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.PathManager
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.JBColor
import java.awt.Color
import java.io.File
import java.io.IOException
import javax.swing.BorderFactory
import javax.swing.SwingUtilities
import javax.swing.SwingWorker
import javax.swing.border.TitledBorder
import javax.swing.JTextArea

class TestCaseRunnerController(
    private val model: TestCaseModel,
    private val projectBaseDir: String,
    private val project: Project
) {
    private val fileTracker = FileTracker(project)
    private var stopRequested = false

    var onStopComplete: (() -> Unit)? = null

    var onExecutionFinished: (() -> Unit)? = null // 콜백을 받을 수 있는 변수 추가

    fun requestStop() {
        stopRequested = true
    }


    fun runAllTestCasesSequentially(testCasePanels: List<TestCaseComponents>, onFinish: () -> Unit) {
        stopRequested = false // 실행 전 stopRequested를 초기화
        onExecutionFinished = onFinish
        runTestCase(0, testCasePanels)
    }

    fun runSelectedTestCasesSequentially(selectedTestCasePanels: List<TestCaseComponents>, onComplete: () -> Unit) {
        stopRequested = false // 실행 전 stopRequested를 초기화
        onExecutionFinished = onComplete
        runTestCase(0, selectedTestCasePanels)
    }

    private fun runTestCase(index: Int, testCasePanels: List<TestCaseComponents>) {
        if (index >= testCasePanels.size) {
            // 모든 테스트 케이스가 실행된 후에만 호출
            SwingUtilities.invokeLater {
                onExecutionFinished?.invoke()
            }
            return
        }

        if (stopRequested) {
            SwingUtilities.invokeLater {
                onStopComplete?.invoke() // 정지가 완료되면 콜백 호출
            }
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
                    fileTracker.getCurrentFile()?.let { file ->
                        val document = FileDocumentManager.getInstance().getDocument(file)
                        document?.let {
                            FileDocumentManager.getInstance().saveDocument(it)
                        }
                    }
                }

                val compileResult = compileIfNeeded(testCase.answerTextArea, testCase.errorTextArea)

                return if (compileResult == "SUCCESS") {
                    startTime = System.currentTimeMillis()
                    runProgramWithCaching(input, expectedOutput, testCase.answerTextArea, testCase.errorTextArea)
                } else {
                    compileResult
                }
            }

            override fun done() {
                if (stopRequested) {
                    SwingUtilities.invokeLater {
                        testCase.panel.border = BorderFactory.createTitledBorder("Stopped")
                        onStopComplete?.invoke()  // 정지 후에만 호출
                    }
                    return
                }

                try {
                    val result = get()
                    endTime = System.currentTimeMillis()
                    val executionTime = endTime - startTime

                    SwingUtilities.invokeLater {
                        testCase.panel.border = when (result) {
                            "AC" -> {
                                testCase.uiStateController?.setState(UIState.UiFolded, executed = true)
                                BorderFactory.createTitledBorder(
                                    BorderFactory.createLineBorder(JBColor.GREEN),
                                    "AC - $executionTime ms",
                                    TitledBorder.DEFAULT_JUSTIFICATION,
                                    TitledBorder.DEFAULT_POSITION,
                                    null,
                                    JBColor.GREEN
                                )
                            }
                            "WA" -> {
                                testCase.uiStateController?.setState(UIState.UiExpanded, executed = true)
                                BorderFactory.createTitledBorder(
                                    BorderFactory.createLineBorder(Color.RED),
                                    "WA - $executionTime ms",
                                    TitledBorder.DEFAULT_JUSTIFICATION,
                                    TitledBorder.DEFAULT_POSITION,
                                    null,
                                    Color.RED
                                )
                            }
                            "CE" -> {
                                testCase.uiStateController?.setState(UIState.UiExpanded, executed = true)
                                BorderFactory.createTitledBorder(
                                    BorderFactory.createLineBorder(Color.MAGENTA),
                                    "CE - $executionTime ms",
                                    TitledBorder.DEFAULT_JUSTIFICATION,
                                    TitledBorder.DEFAULT_POSITION,
                                    null,
                                    Color.MAGENTA
                                )
                            }
                            "RE" -> {
                                testCase.uiStateController?.setState(UIState.UiExpanded, executed = true)
                                BorderFactory.createTitledBorder(
                                    BorderFactory.createLineBorder(Color.ORANGE),
                                    "RE - $executionTime ms",
                                    TitledBorder.DEFAULT_JUSTIFICATION,
                                    TitledBorder.DEFAULT_POSITION,
                                    null,
                                    Color.ORANGE
                                )
                            }
                            else -> {
                                testCase.uiStateController?.setState(UIState.UiExpanded, executed = true)
                                BorderFactory.createTitledBorder("Unknown Error")
                            }
                        }
                    }

                    SwingUtilities.invokeLater {
                        runTestCase(index + 1, testCasePanels)
                    }

                } catch (e: Exception) {
                    SwingUtilities.invokeLater {
                        testCase.panel.border = BorderFactory.createTitledBorder("Error")
                        testCase.errorTextArea.text = "Error during execution: ${e.message}"
                    }
                }
            }
        }.execute()
    }


    private fun compileIfNeeded(answerTextArea: JTextArea, errorTextArea: JTextArea): String {
        val virtualFile = fileTracker.getCurrentFile() ?: return "No file open."

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
