// TestCaseData.kt 수정된 부분
package com.github.rungeun.kcj.kotlincodejudge

data class TestCaseData(
    val testCaseNumber: Int,
    val input: String,
    val output: String,
    val answer: String,
    val cerr: String,
    val result: String
) {
    // 저장 형식으로 변환하는 메서드
// TestCaseData.kt의 formatToSave() 메서드 예시
    fun formatToSave(): String {
        return """
TC$testCaseNumber
Input:
$input
Output:
$output
Answer:
$answer
Cerr:
$cerr
RESULT = $result;
""".trimIndent()
    }


    companion object {
        // 문자열 데이터를 파싱하여 TestCaseData로 변환하는 메서드
        fun fromFormattedString(data: String): TestCaseData? {
            val regex = """TC(\d+)\nInput:\n(.*?)\nOutput:\n(.*?)\nAnswer:\n(.*?)\nCerr:\n(.*?)\nRESULT = (.*?);""".toRegex(RegexOption.DOT_MATCHES_ALL)
            val match = regex.find(data) ?: return null
            val (number, input, output, answer, cerr, result) = match.destructured

            return TestCaseData(
                testCaseNumber = number.toInt(),
                input = input.trim(),
                output = output.trim(),
                answer = answer.trim(),
                cerr = cerr.trim(),
                result = result.trim()
            )
        }
    }
}
