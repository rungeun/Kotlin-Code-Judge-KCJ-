package com.github.rungeun.kcj.kotlincodejudge

import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
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

    fun setupFileChangeListener(onFileChanged: (String) -> Unit) {
        connection.subscribe(VirtualFileManager.VFS_CHANGES, object : BulkFileListener {
            override fun after(events: List<VFileEvent>) {
                for (event in events) {
                    val virtualFile: VirtualFile? = event.file
                    if (virtualFile != null && virtualFile == FileEditorManager.getInstance(project).selectedEditor?.file) {
                        onFileChanged(virtualFile.path)
                    }
                }
            }
        })
    }

    // 현재 열려 있는 파일의 절대 경로를 가져오는 메서드
    private fun getCurrentFilePath(): String? {
        val editor = FileEditorManager.getInstance(project).selectedTextEditor
        if (editor == null) {
            println("No editor found")
            return null
        }
        val document = editor.document
        val file = FileDocumentManager.getInstance().getFile(document)
        if (file == null) {
            println("No file found for document")
            return null
        }
        return file.canonicalPath
    }

    // 입력 값과 실행 출력 값을 저장하는 메서드
    fun saveValues(testCases: List<TestCase>) {
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
            JOptionPane.showMessageDialog(null, "Failed to save values: ${e.message}")
        }
    }

    fun loadValues(): List<TestCase> {
        val testCaseList = mutableListOf<TestCase>()
        val currentFilePath = getCurrentFilePath()

        if (currentFilePath != null) {
            val saveFile = File(currentFilePath.replace(".kt", ".txt"))

            if (saveFile.exists()) {
                try {
                    BufferedReader(FileReader(saveFile)).use { reader ->
                        var currentTestCase: TestCase? = null
                        var currentSection: String? = null

                        reader.forEachLine { line ->
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
                                currentSection != null && currentTestCase != null -> {
                                    when (currentSection) {
                                        "Input" -> currentTestCase!!.input += if (currentTestCase!!.input.isEmpty()) line else "\n$line"
                                        "Output" -> currentTestCase!!.output += if (currentTestCase!!.output.isEmpty()) line else "\n$line"
                                        "Answer" -> currentTestCase!!.answer += if (currentTestCase!!.answer.isEmpty()) line else "\n$line"
                                        "Cerr" -> currentTestCase!!.cerr += if (currentTestCase!!.cerr.isEmpty()) line else "\n$line"
                                    }
                                }
                            }
                        }
                        currentTestCase?.let { testCaseList.add(it) }
                    }
                } catch (e: IOException) {
                    JOptionPane.showMessageDialog(null, "Failed to load values: ${e.message}")
                }
            }
        }

        return testCaseList
    }

    // 파일 변경을 감지하는 리스너 설정
    fun setupFileChangeListener() {
        connection.subscribe(VirtualFileManager.VFS_CHANGES, object : BulkFileListener {
            override fun after(events: List<VFileEvent>) {
                for (event in events) {
                    val virtualFile: VirtualFile? = event.file

                    if (virtualFile != null && virtualFile == FileEditorManager.getInstance(project).selectedEditor?.file) {
                        testCaseManager.addTestCases(loadValues()) // 수정된 부분
                    }
                }
            }
        })
    }
}
