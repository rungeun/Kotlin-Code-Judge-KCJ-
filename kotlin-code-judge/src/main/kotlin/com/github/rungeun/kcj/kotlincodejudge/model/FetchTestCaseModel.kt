package com.github.rungeun.kcj.kotlincodejudge.model

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.io.IOException

class FetchTestCaseModel {

    fun fetchTestCases(problemNumber: String): List<Pair<String, String>>? {
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
            result
        } catch (e: IOException) {
            null
        }
    }
}
