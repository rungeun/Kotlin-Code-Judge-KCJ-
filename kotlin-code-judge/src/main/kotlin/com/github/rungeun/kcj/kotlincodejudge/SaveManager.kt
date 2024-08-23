package com.github.rungeun.kcj.kotlincodejudge

import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.util.messages.MessageBusConnection
import java.io.*
import javax.swing.JOptionPane

data class TestCase(
    var input: String = "",
    var output: String = "",
    var answer: String = "",
    var cerr: String = "",
    var result: String = ""
)

class SaveManager(private val project: Project, private val testCaseManager: TestCaseManager) {
    private val connection: MessageBusConnection = project.messageBus.connect()

    // Method to set up a file change listener
    fun setupFileChangeListener(onFileChanged: (VirtualFile) -> Unit) {
        connection.subscribe(VirtualFileManager.VFS_CHANGES, object : BulkFileListener {
            override fun after(events: List<VFileEvent>) {
                for (event in events) {
                    val virtualFile: VirtualFile? = event.file
                    if (virtualFile != null && virtualFile == FileEditorManager.getInstance(project).selectedEditor?.file) {
                        onFileChanged(virtualFile)
                    }
                }
            }
        })
    }

    // Method to get the current file path
    fun getCurrentFilePath(): String? {
        val editor = FileEditorManager.getInstance(project).selectedTextEditor ?: return null
        val document = editor.document
        val file = FileDocumentManager.getInstance().getFile(document) ?: return null
        return file.canonicalPath
    }

    // 입력 값과 실행 출력 값을 저장하는 메서드
    fun saveTestCases(testCases: List<TestCase>) {
        try {
            val currentFilePath = getCurrentFilePath()

            if (currentFilePath != null) {
                val saveFile = File(currentFilePath.replace(".kt", ".txt"))

                FileWriter(saveFile).use { writer ->
                    testCases.forEachIndexed { index, testCase ->
                        writer.write("TC${index + 1}\n")
                        writer.write("Input:\n${testCase.input}\n")
                        writer.write("Output:\n${testCase.output}\n")
                        writer.write("Answer:\n${testCase.answer}\n")
                        writer.write("Cerr:\n${testCase.cerr}\n")
                        writer.write("RESULT = ${testCase.result};\n\n")
                    }
                }
            }
        } catch (e: IOException) {
            JOptionPane.showMessageDialog(null, "Failed to save test cases: ${e.message}")
        }
    }

    // Method to load test cases from a file
    fun loadValues(file: VirtualFile): List<TestCase> {
        val testCaseList = mutableListOf<TestCase>()

        try {
            val inputStream = file.inputStream
            val reader = BufferedReader(InputStreamReader(inputStream))

            var currentTestCase: TestCase? = null
            var currentSection: String? = null

            reader.useLines { lines ->
                lines.forEach { line ->
                    when {
                        line.startsWith("TC") -> {
                            currentTestCase?.let { testCaseList.add(it) }
                            currentTestCase = TestCase()
                        }
                        line.startsWith("Input:") -> currentSection = "Input"
                        line.startsWith("Output:") -> currentSection = "Output"
                        line.startsWith("Answer:") -> currentSection = "Answer"
                        line.startsWith("Cerr:") -> currentSection = "Cerr"
                        line.startsWith("RESULT =") -> {
                            currentTestCase?.result = line.substringAfter("RESULT = ").removeSuffix(";")
                            currentSection = null
                        }
                        currentSection != null -> {
                            // Safely unwrap currentTestCase before using it
                            val testCase = currentTestCase
                            if (testCase != null) {
                                when (currentSection) {
                                    "Input" -> testCase.input += if (testCase.input.isEmpty()) line else "\n$line"
                                    "Output" -> testCase.output += if (testCase.output.isEmpty()) line else "\n$line"
                                    "Answer" -> testCase.answer += if (testCase.answer.isEmpty()) line else "\n$line"
                                    "Cerr" -> testCase.cerr += if (testCase.cerr.isEmpty()) line else "\n$line"
                                }
                            }
                        }
                    }
                }
            }
            currentTestCase?.let { testCaseList.add(it) }

        } catch (e: Exception) {
            e.printStackTrace()
            JOptionPane.showMessageDialog(null, "Failed to load values: ${e.message}")
        }

        return testCaseList
    }

    // Overloaded method to load test cases based on the current file path
    fun loadValues(): List<TestCase> {
        val currentFilePath = getCurrentFilePath() ?: return emptyList()
        val saveFile = File(currentFilePath.replace(".kt", ".txt"))

        // Use LocalFileSystem to find the VirtualFile
        val virtualFile = LocalFileSystem.getInstance().findFileByIoFile(saveFile)
        return if (virtualFile != null && virtualFile.exists()) {
            loadValues(virtualFile)
        } else {
            emptyList()
        }
    }
}
