package com.github.rungeun.kcj.kotlincodejudge.controller

import com.github.rungeun.kcj.kotlincodejudge.model.FetchTestCaseModel
import com.github.rungeun.kcj.kotlincodejudge.model.TestCaseModel
import com.github.rungeun.kcj.kotlincodejudge.view.MainToolWindowUI
import com.github.rungeun.kcj.kotlincodejudge.view.TestCasePanelUI
import javax.swing.JPanel

class MainController(private val ui: MainToolWindowUI) {
    private val model = TestCaseModel()
    private var testCaseController: TestCaseController

    private val fetchTestCaseModel = FetchTestCaseModel()
    private val fetchTestCaseController: FetchTestCaseController
    init {
        // 초기화
        val initialTestCasePanelUI = TestCasePanelUI(0) // 초기화용 기본 패널
        testCaseController = TestCaseController(model, initialTestCasePanelUI)
        fetchTestCaseController = FetchTestCaseController(fetchTestCaseModel, ui, testCaseController)

        ui.donateButton.addActionListener(GiveCoffeeActionListener())
        ui.guideButton.addActionListener(GuideActionListener())

        ui.fetchButton.addActionListener {
                fetchTestCaseController.onFetchTestCasesButtonClick()
        }

        ui.newTestCaseButton.addActionListener {
                // 새로운 TestCasePanelUI와 함께 TestCaseController의 기존 인스턴스를 사용하여 테스트 케이스 추가
                val testCaseNumber = model.getAllTestCaseComponents().size + 1
                val testCasePanelUI = TestCasePanelUI(testCaseNumber)
                testCaseController = TestCaseController(model, testCasePanelUI) // 새로운 UI로 업데이트
                val newTestCasePanel = testCaseController.createAndAddTestCasePanel(
                    testCaseNumber = testCaseNumber
                ).panel

                ui.addTestCasePanel(newTestCasePanel)
        }

        ui.selectAll.addActionListener { testCaseController.selectAllTestCases(true) }
        ui.clearSelection.addActionListener { testCaseController.selectAllTestCases(false) }
    }


}
