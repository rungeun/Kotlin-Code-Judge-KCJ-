// TestCaseSaver.kt
package com.github.rungeun.kcj.kotlincodejudge.controller

import com.github.rungeun.kcj.kotlincodejudge.TestCaseComponents
import com.github.rungeun.kcj.kotlincodejudge.TestCaseData
import java.io.File
import java.io.IOException
import javax.swing.border.TitledBorder

class TestCaseSaver(private val fileTracker: FileTracker) {

    // Save test cases to a specific file
    fun saveTestCases(testCases: List<TestCaseData>) {
        val kotlinFile = fileTracker.getCurrentFile() ?: return
        val parentDir = kotlinFile.parent
        val buildDir = File(parentDir.path, "buildc")

        // 디렉토리와 파일 생성 여부를 체크하고 필요한 경우 즉시 생성
        if (!buildDir.exists() && !buildDir.mkdirs()) {
            println("Error: Could not create buildc directory.")
            return
        }

        val testCaseFile = File(buildDir, "${kotlinFile.nameWithoutExtension}.txt")

        try {
            if (!testCaseFile.exists()) {
                testCaseFile.createNewFile()
            }
            testCaseFile.writeText(testCases.joinToString("\n\n") { it.formatToSave() })
        } catch (e: IOException) {
            println("Error saving test cases: ${e.message}")
        }
    }

    // Load test cases from the file associated with the current Kotlin file
    fun loadTestCases(): List<TestCaseData> {
        val kotlinFile = fileTracker.getCurrentFile() ?: return emptyList()
        val parentDir = kotlinFile.parent
        val buildDir = File(parentDir.path, "buildc")
        val testCaseFile = File(buildDir, "${kotlinFile.nameWithoutExtension}.txt")

        if (!testCaseFile.exists()) {
            return emptyList()
        }

        return try {
            val data = testCaseFile.readText()
            parseTestCaseData(data)
        } catch (e: IOException) {
            println("Error loading test cases: ${e.message}")
            emptyList()
        }
    }

    // Converts TestCaseComponents to TestCaseData
    private fun convertToTestCaseData(testCaseComponent: TestCaseComponents): TestCaseData {
        val borderTitle = (testCaseComponent.panel.border as? TitledBorder)?.title ?: "UNKNOWN"

        return TestCaseData(
            testCaseNumber = extractTestCaseNumber(testCaseComponent),
            input = testCaseComponent.inputTextArea.text,
            output = testCaseComponent.outputTextArea.text,
            answer = testCaseComponent.answerTextArea.text,
            cerr = testCaseComponent.errorTextArea.text,
            result = borderTitle // Correctly handles the border title extraction
        )
    }

    // Helper function to extract the test case number
    private fun extractTestCaseNumber(testCaseComponent: TestCaseComponents): Int {
        val title = (testCaseComponent.panel.border as? TitledBorder)?.title ?: "UNKNOWN"
        return title.replace("TC", "").toIntOrNull() ?: 0
    }

    private fun parseTestCaseData(data: String): List<TestCaseData> {
        // 데이터를 구분하여 각 블록을 추출
        val testCases = mutableListOf<TestCaseData>()
        val blocks = data.split("\n\n").map { it.trim() }

        // 각 블록을 TestCaseData 객체로 변환하여 리스트에 추가
        for (block in blocks) {
            val testCase = TestCaseData.fromFormattedString(block)
            if (testCase != null) {
                testCases.add(testCase)
            }
        }
        return testCases
    }
}
