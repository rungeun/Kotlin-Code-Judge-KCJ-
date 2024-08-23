package com.github.rungeun.kcj.kotlincodejudge.view

import com.github.rungeun.kcj.kotlincodejudge.TestCaseComponents
import com.github.rungeun.kcj.kotlincodejudge.UIStateManager
import javax.swing.*
import java.awt.Color
import com.intellij.ui.components.JBScrollPane
import java.awt.Dimension

class TestCasePanelUI {
    // 각각의 텍스트 패널을 생성하는 함수
    private fun createTextPanel(labelText: String, textArea: JTextArea, initialText: String = ""): JPanel {
        textArea.text = initialText

        val copyButton = JButton("Copy").apply {
            isOpaque = false
            background = Color(0, 0, 0, 0)
            border = BorderFactory.createEmptyBorder()
        }

        val labelPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            add(JLabel(labelText))
            add(Box.createHorizontalGlue())
            add(copyButton)
        }

        val scrollPane = JBScrollPane(textArea).apply {
            preferredSize = Dimension(120, 60)
            maximumSize = Dimension(Int.MAX_VALUE, 60)
        }

        return JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            add(labelPanel)
            add(scrollPane)
        }
    }

    // 테스트 케이스 패널을 생성하는 함수
    fun createTestCasePanel(
        testCaseNumber: Int,
        inputText: String,
        outputText: String
    ): TestCaseComponents {
        val inputTextArea = JTextArea(3, 5)
        val outputTextArea = JTextArea(3, 5)
        val answerTextArea = JTextArea(3, 5)
        val errorTextArea = JTextArea(3, 5)
        val selectTestCase = JCheckBox()
        val uiStateButton = JButton("UI state changes")

        val testCasePanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            border = BorderFactory.createTitledBorder("TestCase $testCaseNumber")
            add(createTestCaseRow1Panel(testCaseNumber, selectTestCase, uiStateButton))
            add(createTextPanel("In", inputTextArea, inputText))
            add(createTextPanel("Out", outputTextArea, outputText))
            add(createTextPanel("Answer", answerTextArea))
            add(createTextPanel("Cerr", errorTextArea))
        }

        val uiStateManager = UIStateManager(
            testCasePanel,
            createTextPanel("In", inputTextArea, inputText),
            createTextPanel("Out", outputTextArea, outputText),
            createTextPanel("Answer", answerTextArea),
            createTextPanel("Cerr", errorTextArea),
            uiStateButton
        )

        return TestCaseComponents(
            testCasePanel,
            selectTestCase,
            inputTextArea,
            outputTextArea,
            answerTextArea,
            errorTextArea,
            uiStateManager
        )
    }

    // 테스트 케이스의 첫 번째 행 패널을 생성하는 함수
    private fun createTestCaseRow1Panel(
        testCaseNumber: Int,
        checkBox: JCheckBox,
        uiStateButton: JButton
    ): JPanel {
        val testCaseLabel = JLabel("UTC $testCaseNumber")

        val topRowPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            isOpaque = false
            add(checkBox)
            add(testCaseLabel)
            add(Box.createHorizontalGlue())
            add(uiStateButton)
        }

        val deleteTestCaseButton = JButton("Delete")

        val bottomRowPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            isOpaque = false
            add(Box.createHorizontalGlue())
            add(deleteTestCaseButton)
        }

        return JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            isOpaque = false
            add(topRowPanel)
            add(bottomRowPanel)
        }
    }
}
