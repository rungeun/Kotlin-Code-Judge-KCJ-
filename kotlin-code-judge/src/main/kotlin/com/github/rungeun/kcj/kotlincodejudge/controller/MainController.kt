package com.github.rungeun.kcj.kotlincodejudge.controller

import com.github.rungeun.kcj.kotlincodejudge.model.TestCaseModel
import com.github.rungeun.kcj.kotlincodejudge.view.MainToolWindowUI
import com.github.rungeun.kcj.kotlincodejudge.view.TestCasePanelUI
import javax.swing.JPanel

class MainController(private val ui: MainToolWindowUI) {
    private val model = TestCaseModel()

    init {
        ui.donateButton.addActionListener(GiveCoffeeActionListener())
        ui.guideButton.addActionListener(GuideActionListener())
        ui.newTestCaseButton.addActionListener {
            // 새로운 테스트 케이스 UI를 생성하고 추가
            val testCaseNumber = model.getAllTestCaseComponents().size + 1
            val testCasePanelUI = TestCasePanelUI(testCaseNumber) // testCaseNumber 전달
            val newTestCasePanel = TestCaseController(model, testCasePanelUI).createAndAddTestCasePanel(
                testCaseNumber = testCaseNumber
            ).panel // TestCaseComponents의 panel 속성을 가져와 추가

            // 생성된 패널을 View에 추가
            ui.addTestCasePanel(newTestCasePanel)
        }
    }

    private fun removeTestCaseComponent(panel: JPanel) {
        val testCaseComponent = model.getAllTestCaseComponents().find { it.panel == panel }
        if (testCaseComponent != null) {
            model.removeTestCaseComponent(testCaseComponent)
            ui.removeTestCasePanel(panel) // UI에서 패널 제거
        }
    }
}
