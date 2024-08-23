//MainController.kt
package com.github.rungeun.kcj.kotlincodejudge.controller

import com.github.rungeun.kcj.kotlincodejudge.model.TestCaseModel
import com.github.rungeun.kcj.kotlincodejudge.view.MainToolWindowUI
import com.github.rungeun.kcj.kotlincodejudge.view.TestCasePanelUI
import javax.swing.JPanel

class MainController(private val ui: MainToolWindowUI) {
    private val model = TestCaseModel()
    private val testCaseController = TestCaseController(model, TestCasePanelUI())

    init {
        ui.donateButton.addActionListener(GiveCoffeeActionListener())
        ui.guideButton.addActionListener(GuideActionListener())
        //ui.copyTextButton.addActionListener(CopyTextActionListener())

        ui.newTestCaseButton.addActionListener {
            val newTestCasePanel = testCaseController.createAndAddTestCasePanel(
                testCaseNumber = model.getAllTestCaseComponents().size + 1,
                inputText = "",
                outputText = ""
            )
            ui.addTestCasePanel(newTestCasePanel)
        }
    }
    private fun removeTestCaseComponent(panel: JPanel) {
        val testCaseComponent = model.getAllTestCaseComponents().find { it.panel == panel }
        if (testCaseComponent != null) {
            model.removeTestCaseComponent(testCaseComponent)
            ui.removeTestCasePanel(panel)
        }
    }

}
