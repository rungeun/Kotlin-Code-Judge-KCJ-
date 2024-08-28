package com.github.rungeun.kcj.kotlincodejudge.controller

import com.github.rungeun.kcj.kotlincodejudge.TestCaseComponents
import com.github.rungeun.kcj.kotlincodejudge.UIState
import com.github.rungeun.kcj.kotlincodejudge.UIStateController
import com.github.rungeun.kcj.kotlincodejudge.model.TestCaseModel
import com.github.rungeun.kcj.kotlincodejudge.view.TestCasePanelUI
import com.intellij.ui.components.JBScrollPane
import java.awt.Dimension
import javax.swing.*

class TestCaseController(
    val model: TestCaseModel,
    private val ui: TestCasePanelUI
) {

    init {
        ui.deleteTestCaseButton.addActionListener {
            removeTestCase(ui.testCasePanel)
        }
    }

    fun createAndAddTestCasePanel(
        testCaseNumber: Int,
        inputText: String = "입력창",
        outputText: String = "출력창",
        answerLabel: String = "결과창",
        errorLabel: String = "에러창"
    ): TestCaseComponents {
        // 새로운 UI 요소 초기화
        val testCasePanelUI = TestCasePanelUI(testCaseNumber)

        // deleteTestCaseButton에 리스너 등록
        testCasePanelUI.deleteTestCaseButton.addActionListener {
            println("클릭 감지 됨")
            removeTestCase(testCasePanelUI.testCasePanel)
        }

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

        val topButtonPanel2 = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            add(Box.createHorizontalGlue())
            add(testCasePanelUI.deleteTestCaseButton)
        }

        // 입출력 영역 생성
        val inputPanel = createTextPanel(testCasePanelUI.inputLabel, testCasePanelUI.inputTextArea, testCasePanelUI.inputCopyButton)
        val outputPanel = createTextPanel(testCasePanelUI.outputLabel, testCasePanelUI.outputTextArea, testCasePanelUI.outputCopyButton)
        val answerPanel = createTextPanel(testCasePanelUI.answerLabel, testCasePanelUI.answerTextArea, testCasePanelUI.answerCopyButton)
        val errorPanel = createTextPanel(testCasePanelUI.errorLabel, testCasePanelUI.errorTextArea, testCasePanelUI.errorCopyButton)

        testCasePanel.add(topButtonPanel)
        testCasePanel.add(topButtonPanel2)
        testCasePanel.add(inputPanel)
        testCasePanel.add(outputPanel)
        testCasePanel.add(answerPanel)
        testCasePanel.add(errorPanel)

        // Copy 버튼에 대한 이벤트 리스너 추가
        testCasePanelUI.inputCopyButton.addActionListener(CopyTextActionListener(testCasePanelUI.inputCopyButton) { testCasePanelUI.inputTextArea.text })
        testCasePanelUI.outputCopyButton.addActionListener(CopyTextActionListener(testCasePanelUI.outputCopyButton) { testCasePanelUI.outputTextArea.text })
        testCasePanelUI.answerCopyButton.addActionListener(CopyTextActionListener(testCasePanelUI.answerCopyButton) { testCasePanelUI.answerTextArea.text })
        testCasePanelUI.errorCopyButton.addActionListener(CopyTextActionListener(testCasePanelUI.errorCopyButton) { testCasePanelUI.errorTextArea.text })

        // UIStateController 초기화
        val uiStateController = UIStateController(
            testCasePanel = testCasePanel,
            inputPanel = inputPanel,
            outputPanel = outputPanel,
            answerPanel = answerPanel,
            errorPanel = errorPanel,
            uiStateButton = testCasePanelUI.uiStateButton
        )

        // 초기 상태를 UiMidway로 설정
        uiStateController.setState(UIState.UiMidway)

        // TestCaseComponents 객체 생성 및 반환
        val testCaseComponents = TestCaseComponents(
            panel = testCasePanel,
            selectTestCase = testCasePanelUI.selectTestCase,
            inputTextArea = testCasePanelUI.inputTextArea,
            outputTextArea = testCasePanelUI.outputTextArea,
            answerTextArea = testCasePanelUI.answerTextArea,
            errorTextArea = testCasePanelUI.errorTextArea,
            uiStateController = uiStateController
        )

        // Model에 추가
        model.addTestCaseComponent(testCaseComponents)

        // UI 갱신을 강제하여 변경 사항이 즉시 반영되도록 합니다.
        SwingUtilities.invokeLater {
            testCasePanel.revalidate()
            testCasePanel.repaint()
        }

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

    private fun removeTestCase(panel: JPanel) {
        println("removeTestCase 실행됨")
        val testCaseComponent = model.getAllTestCaseComponents().find { it.panel == panel }
        if (testCaseComponent != null) {
            model.removeTestCaseComponent(testCaseComponent)

            val parent = panel.parent as? JPanel
            parent?.remove(panel)

            val rootContainer = parent?.topLevelAncestor as? JPanel
            rootContainer?.revalidate()
            rootContainer?.repaint()

            renumberTestCases() // Controller에서 Model 데이터를 기반으로 View를 갱신
        } else {
            println("Error: testCaseComponent not found")
        }
    }

    private fun renumberTestCases() {
        println("renumberTestCases 실행됨")
        model.getAllTestCaseComponents().forEachIndexed { index, testCase ->
            // Model에서 가져온 데이터를 View에게 전달
            ui.updateTestCaseLabel(testCase.panel, index + 1)
        }
    }

    fun selectAllTestCases(select: Boolean) {
        model.getAllTestCaseComponents().forEach { it.selectTestCase.isSelected = select }
    }
}
