package com.github.rungeun.kcj.kotlincodejudge.controller

import com.github.rungeun.kcj.kotlincodejudge.model.TestCaseModel
import com.github.rungeun.kcj.kotlincodejudge.view.TestCasePanelUI
import com.github.rungeun.kcj.kotlincodejudge.controller.CopyTextActionListener
import javax.swing.JButton
import javax.swing.JPanel

class TestCaseController(
    private val model: TestCaseModel,
    private val ui: TestCasePanelUI
) {

    fun createAndAddTestCasePanel(testCaseNumber: Int, inputText: String, outputText: String): JPanel {
        val testCaseComponents = ui.createTestCasePanel(testCaseNumber, inputText, outputText)

        // 설정된 버튼에 대한 이벤트 리스너를 Controller에서 설정합니다.
        testCaseComponents.apply {
            inputTextArea.getComponents().forEach { component ->
                if (component is JButton && component.text == "Copy") {
                    component.addActionListener(CopyTextActionListener(component) { inputTextArea.text })
                }
            }

            outputTextArea.getComponents().forEach { component ->
                if (component is JButton && component.text == "Copy") {
                    component.addActionListener(CopyTextActionListener(component) { outputTextArea.text })
                }
            }

            answerTextArea.getComponents().forEach { component ->
                if (component is JButton && component.text == "Copy") {
                    component.addActionListener(CopyTextActionListener(component) { answerTextArea.text })
                }
            }

            errorTextArea.getComponents().forEach { component ->
                if (component is JButton && component.text == "Copy") {
                    component.addActionListener(CopyTextActionListener(component) { errorTextArea.text })
                }
            }
        }

        model.addTestCaseComponent(testCaseComponents)
        return testCaseComponents.panel
    }

    private fun removeTestCaseComponentByPanel(panel: JPanel) {
        val testCaseComponent = model.getAllTestCaseComponents().find { it.panel == panel }
        if (testCaseComponent != null) {
            model.removeTestCaseComponent(testCaseComponent)
          //  ui.removeTestCasePanel(panel)
        }
    }
}
