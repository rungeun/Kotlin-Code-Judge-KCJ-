package com.github.rungeun.kcj.kotlincodejudge.controller

import com.github.rungeun.kcj.kotlincodejudge.TestCaseComponents
import com.github.rungeun.kcj.kotlincodejudge.model.TestCaseModel
import com.github.rungeun.kcj.kotlincodejudge.view.TestCasePanelUI
import com.intellij.ui.components.JBScrollPane
import java.awt.Dimension
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.JLabel
import javax.swing.JTextArea
import javax.swing.BoxLayout
import javax.swing.Box

class TestCaseController(
    val model: TestCaseModel,
    private val ui: TestCasePanelUI
) {

    fun createAndAddTestCasePanel(
        testCaseNumber: Int,
        inputText: String = "입력창",
        outputText: String = "출력창",
        answerLabel: String = "결과창",
        errorLabel: String = "에러창"
    ): TestCaseComponents {
        // 새로운 UI 요소 초기화
        val testCasePanelUI = TestCasePanelUI(testCaseNumber)

        // TextArea에 초기값 설정
        testCasePanelUI.inputTextArea.text = inputText
        testCasePanelUI.outputTextArea.text = outputText
        testCasePanelUI.answerTextArea.text = answerLabel
        testCasePanelUI.errorTextArea.text = errorLabel

        // 패널 초기화
        val testCasePanel = testCasePanelUI.testCasePanel

        // Label 및 ScrollPane 추가
        val topButtonPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            add(testCasePanelUI.selectTestCase)
            add(testCasePanelUI.utcLabel)
            add(Box.createHorizontalGlue())
            add(testCasePanelUI.uiStateButton)
        }

        // 입출력 영역 생성
        testCasePanel.add(topButtonPanel)
        testCasePanel.add(
            createTextPanel(
                testCasePanelUI.inputLabel,
                testCasePanelUI.inputTextArea,
                testCasePanelUI.inputCopyButton
            )
        )
        testCasePanel.add(
            createTextPanel(
                testCasePanelUI.outputLabel,
                testCasePanelUI.outputTextArea,
                testCasePanelUI.outputCopyButton
            )
        )
        testCasePanel.add(
            createTextPanel(
                testCasePanelUI.answerLabel,
                testCasePanelUI.answerTextArea,
                testCasePanelUI.answerCopyButton
            )
        )
        testCasePanel.add(
            createTextPanel(
                testCasePanelUI.errorLabel,
                testCasePanelUI.errorTextArea,
                testCasePanelUI.errorCopyButton
            )
        )

        // Copy 버튼에 대한 이벤트 리스너 추가
        testCasePanelUI.inputCopyButton.addActionListener(CopyTextActionListener(testCasePanelUI.inputCopyButton) { testCasePanelUI.inputTextArea.text })
        testCasePanelUI.outputCopyButton.addActionListener(CopyTextActionListener(testCasePanelUI.outputCopyButton) { testCasePanelUI.outputTextArea.text })
        testCasePanelUI.answerCopyButton.addActionListener(CopyTextActionListener(testCasePanelUI.answerCopyButton) { testCasePanelUI.answerTextArea.text })
        testCasePanelUI.errorCopyButton.addActionListener(CopyTextActionListener(testCasePanelUI.errorCopyButton) { testCasePanelUI.errorTextArea.text })

        // TestCaseComponents 객체 생성 및 반환
        val testCaseComponents = TestCaseComponents(
            panel = testCasePanel,
            selectTestCase = testCasePanelUI.selectTestCase,
            inputTextArea = testCasePanelUI.inputTextArea,
            outputTextArea = testCasePanelUI.outputTextArea,
            answerTextArea = testCasePanelUI.answerTextArea,
            errorTextArea = testCasePanelUI.errorTextArea,
            uiStateManager = null // 필요시 uiStateManager 초기화
        )

        // Model에 추가
        model.addTestCaseComponent(testCaseComponents)

        return testCaseComponents
    }

    private fun createTextPanel(label: JLabel, textArea: JTextArea, copyButton: JButton): JPanel {
        return JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            add(JPanel().apply {
                layout = BoxLayout(this, BoxLayout.X_AXIS)
                add(label)
                add(Box.createHorizontalGlue())
                add(copyButton)
            })
            add(JBScrollPane(textArea).apply {
                preferredSize = Dimension(120, 60)
                maximumSize = Dimension(Int.MAX_VALUE, 60)
            })
        }
    }

    private fun removeTestCaseComponentByPanel(panel: JPanel) {
        val testCaseComponent = model.getAllTestCaseComponents().find { it.panel == panel }
        if (testCaseComponent != null) {
            model.removeTestCaseComponent(testCaseComponent)
            // UI 요소를 제거하는 메서드를 명시적으로 작성
            ui.testCasePanel.remove(panel)
            ui.testCasePanel.revalidate()
            ui.testCasePanel.repaint()
        }
    }

    fun selectAllTestCases(select: Boolean) {
        model.getAllTestCaseComponents().forEach { it.selectTestCase.isSelected = select }
    }
}
