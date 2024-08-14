package com.github.rungeun.kcj.kotlincodejudge

import com.intellij.openapi.application.PathManager
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import java.io.File
import java.io.IOException
import javax.swing.*

class TestCaseRunner(private val projectBaseDir: String, private val project: Project) {

    fun runAllTestCasesSequentially(testCasePanels: List<TestCaseComponents>) {
        runTestCase(0, testCasePanels)
    }

    private fun runTestCase(index: Int, testCasePanels: List<TestCaseComponents>) {
        if (index >= testCasePanels.size) return

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
                try {
                    val result = get() // runTestCase에서 반환된 결과
                    endTime = System.currentTimeMillis()
                    val executionTime = endTime - startTime

                    when (result) {
                        "CE" -> testCase.panel.border = BorderFactory.createTitledBorder("CE - $executionTime ms")
                        "AC" -> testCase.panel.border = BorderFactory.createTitledBorder("AC - $executionTime ms")
                        "WA" -> testCase.panel.border = BorderFactory.createTitledBorder("WA - $executionTime ms")
                        "RE" -> testCase.panel.border = BorderFactory.createTitledBorder("RE - $executionTime ms")
                        else -> testCase.panel.border = BorderFactory.createTitledBorder("Unknown Error")
                    }

                    if (testCase.answerTextArea.text.isBlank() && testCase.errorTextArea.text.isBlank()) {
                        testCase.answerTextArea.text = "No output."
                    }
                } catch (e: Exception) {
                    testCase.panel.border = BorderFactory.createTitledBorder("Error")
                    testCase.errorTextArea.text = "Error during execution: ${e.message}"
                } finally {
                    runTestCase(index + 1, testCasePanels) // 다음 테스트 케이스 실행
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

            // 실제 출력을 Answer 텍스트 영역에 설정
            answerTextArea.text = actualOutput

            // 실행 중에 발생한 에러가 있으면 "RE" 반환 (System.err와 관계 없이 비교는 진행)
            val isRuntimeError = errOutput.isNotBlank() && errOutput.contains("Exception")

            // AC 또는 WA 판별
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
        // 각 줄을 처리한 후 바로 비교
        val normalizedActualOutput = actualOutput.lines().map { it.trimEnd() }.filter { it.isNotEmpty() }.joinToString("\n")
        val normalizedExpectedOutput = expectedOutput.lines().map { it.trimEnd() }.filter { it.isNotEmpty() }.joinToString("\n")

        return normalizedActualOutput == normalizedExpectedOutput
    }
}
