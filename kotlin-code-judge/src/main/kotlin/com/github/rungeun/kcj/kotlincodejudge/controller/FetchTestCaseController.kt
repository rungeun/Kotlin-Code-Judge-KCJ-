package com.github.rungeun.kcj.kotlincodejudge.controller

import com.github.rungeun.kcj.kotlincodejudge.model.FetchTestCaseModel
import com.github.rungeun.kcj.kotlincodejudge.view.MainToolWindowUI

class FetchTestCaseController(
    private val model: FetchTestCaseModel,
    private val ui: MainToolWindowUI,
    private val testCaseController: TestCaseController
) {

    fun onFetchTestCasesButtonClick() {
        val problemNumber = ui.fetchTextField.text // ui에서 문제 번호 가져오기
        val testCases = model.fetchTestCases(problemNumber)
        if (testCases != null) {
            for ((input, output) in testCases) {
                testCaseController.createAndAddTestCasePanel(
                    testCaseNumber = testCaseController.model.getAllTestCaseComponents().size + 1,
                    inputText = input,
                    outputText = output
                ).panel.also {
                    ui.addTestCasePanel(it)
                }
            }
            ui.fetchLabel.text = "ProblemNumber: $problemNumber"
        } else {
            ui.fetchLabel.text = "ProblemNumber: Fail"
        }
    }
}
