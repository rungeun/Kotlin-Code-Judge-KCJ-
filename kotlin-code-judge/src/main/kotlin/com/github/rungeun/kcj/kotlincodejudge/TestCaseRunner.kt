package com.github.rungeun.kcj.kotlincodejudge

import com.intellij.openapi.application.PathManager
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.JBColor
import java.awt.Color // Correct import
import java.io.File
import java.io.IOException
import javax.swing.*
import javax.swing.border.TitledBorder

class TestCaseRunner(
    private val projectBaseDir: String,
    private val project: Project,
    private val onExecutionFinished: () -> Unit, // 모든 실행이 끝난 후 호출할 콜백
    private val onTestCaseFinished: (Int, String) -> Unit // 각 테스트 케이스가 끝날 때 호출할 콜백
) {
    private var stopRequested = false

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
            onExecutionFinished() // 모든 테스트가 끝난 후 버튼 상태를 복원
            return
        }

        val testCase = testCasePanels[index]
        val input = testCase.inputTextArea.text
        val expectedOutput = testCase.outputTextArea.text

        object : SwingWorker<String, Void>() {
            private var startTime: Long = 0
            private var endTime: Long = 0

            override fun doInBackground(): String {
                startTime = System.currentTimeMillis()

                SwingUtilities.invokeLater {
                    testCase.panel.border = BorderFactory.createTitledBorder("Judging...")
                }

                testCase.answerTextArea.text = ""
                testCase.errorTextArea.text = ""

                return runTestCase(input, expectedOutput, testCase.answerTextArea, testCase.errorTextArea)
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
                            else -> BorderFactory.createTitledBorder("Unknown Error")
                        }
                    }

                    // 결과에 따라 UI 상태를 변경
                    SwingUtilities.invokeLater {
                        onTestCaseFinished(index, result)
                    }

                } catch (e: Exception) {
                    SwingUtilities.invokeLater {
                        testCase.panel.border = BorderFactory.createTitledBorder("Error")
                        testCase.errorTextArea.text = "Error during execution: ${e.message}"
                    }
                } finally {
                    // 다음 테스트 케이스를 실행
                    runTestCase(index + 1, testCasePanels)
                }
            }
        }.execute()
    }

    private fun runTestCase(input: String, expectedOutput: String, answerTextArea: JTextArea, errorTextArea: JTextArea): String {
        val editor = EditorFactory.getInstance().allEditors.firstOrNull { it.project == project } ?: return "No file open."
        val document = editor.document
        val virtualFile: VirtualFile = FileDocumentManager.getInstance().getFile(document) ?: return "Unable to get the VirtualFile."
        val code = document.text

        val buildDir = File("$projectBaseDir/buildtc")
        if (!buildDir.exists()) {
            buildDir.mkdirs()
        }
        val sourceFile = File(buildDir, "TempProgram.kt")
        sourceFile.writeText(code)

        return try {
            val ideaHome = PathManager.getHomePath() ?: throw IOException("IntelliJ IDEA installation path not found.")
            val kotlincPath = "$ideaHome/plugins/Kotlin/kotlinc/bin/kotlinc.bat"
            val jarFile = File(buildDir, "TempProgram.jar")

            if (!jarFile.exists() || jarFile.lastModified() < sourceFile.lastModified()) {
                val compileProcess = ProcessBuilder(kotlincPath, sourceFile.absolutePath, "-include-runtime", "-d", jarFile.absolutePath)
                    .directory(buildDir)
                    .redirectErrorStream(true)
                    .start()
                compileProcess.waitFor()

                if (compileProcess.exitValue() != 0) {
                    val errorOutput = compileProcess.inputStream.bufferedReader().readText()
                    errorTextArea.text = "Compilation Errors:\n$errorOutput"
                    return "CE"
                }
            }

            val runProcess = ProcessBuilder("java", "-jar", jarFile.absolutePath)
                .directory(buildDir)
                .redirectInput(ProcessBuilder.Redirect.PIPE)
                .redirectOutput(File(buildDir, "TempProgramOutput.txt"))
                .redirectError(File(buildDir, "TempProgramErr.txt"))
                .start()

            runProcess.outputStream.bufferedWriter().use { writer ->
                writer.write(input)
            }
            runProcess.waitFor()

            val errOutput = File(buildDir, "TempProgramErr.txt").readText()
            errorTextArea.text = errOutput

            val actualOutput = File(buildDir, "TempProgramOutput.txt").readText()

            answerTextArea.text = actualOutput

            val isRuntimeError = errOutput.isNotBlank() && errOutput.contains("Exception")
            val isCorrect = compareOutputs(actualOutput, expectedOutput)

            return when {
                isRuntimeError -> "RE"
                isCorrect -> "AC"
                else -> "WA"
            }
        } catch (e: IOException) {
            errorTextArea.text = "Error during execution: ${e.message}"
            return "CE"
        } catch (e: Exception) {
            errorTextArea.text = "Runtime Error: ${e.message}"
            return "RE"
        } finally {
            sourceFile.delete()
            File(buildDir, "TempProgramOutput.txt").delete()
            File(buildDir, "TempProgramErr.txt").delete()
        }
    }

    private fun compareOutputs(actualOutput: String, expectedOutput: String): Boolean {
        val normalizedActualOutput = actualOutput.lines().map { it.trimEnd() }.filter { it.isNotEmpty() }.joinToString("\n")
        val normalizedExpectedOutput = expectedOutput.lines().map { it.trimEnd() }.filter { it.isNotEmpty() }.joinToString("\n")
        return normalizedActualOutput == normalizedExpectedOutput
    }
}
