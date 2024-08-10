package com.github.rungeun.kcj.kotlincodejudge

import com.intellij.ui.components.JBLabel
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.io.IOException
import javax.swing.JTextField

class FetchTestCaseActionListener(
    private val problemNumberField: JTextField,
    private val testCaseManager: TestCaseManager,
    private val fetchLabel: JBLabel
) : java.awt.event.ActionListener {

    override fun actionPerformed(e: java.awt.event.ActionEvent?) {
        val problemNumber = problemNumberField.text
        val testCases = fetchTestCases(problemNumber)
        if (testCases != null) {
            for ((input, output) in testCases) {
                testCaseManager.addNewTestCase(input, output)
            }
        }
    }

    private fun fetchTestCases(problemNumber: String): List<Pair<String, String>>? {
        val url = "https://www.acmicpc.net/problem/$problemNumber"
        return try {
            val doc: Document = Jsoup.connect(url).get()

            val examples: Elements = doc.select("pre.sampledata")
            val result = mutableListOf<Pair<String, String>>()
            for (i in examples.indices step 2) {
                val input = examples[i].text()
                val output = if (i + 1 < examples.size) examples[i + 1].text() else ""
                result.add(input to output)
            }

            fetchLabel.text = "ProblemNumber: $problemNumber"
            result
        } catch (e: IOException) {
            fetchLabel.text = "ProblemNumber: Fail"
            null
        }
    }
}
