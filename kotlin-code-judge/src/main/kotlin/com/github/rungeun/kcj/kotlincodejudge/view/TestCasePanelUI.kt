package com.github.rungeun.kcj.kotlincodejudge.view

import javax.swing.*
import java.awt.Dimension
import com.intellij.ui.components.JBScrollPane

class TestCasePanelUI(testCaseNumber: Int) {

    // UI 요소 선언
    val testCasePanel: JPanel = JPanel()
    val selectTestCase: JCheckBox = JCheckBox()
    val deleteTestCaseButton: JButton = JButton("Delete")
    val uiStateButton: JButton = JButton("UI state changes")

    val inputTextArea: JTextArea = JTextArea(3, 5)
    val outputTextArea: JTextArea = JTextArea(3, 5)
    val answerTextArea: JTextArea = JTextArea(3, 5)
    val errorTextArea: JTextArea = JTextArea(3, 5)

    val inputCopyButton: JButton = JButton("Copy")
    val outputCopyButton: JButton = JButton("Copy")
    val answerCopyButton: JButton = JButton("Copy")
    val errorCopyButton: JButton = JButton("Copy")

    val inputScrollPane: JBScrollPane
    val outputScrollPane: JBScrollPane
    val answerScrollPane: JBScrollPane
    val errorScrollPane: JBScrollPane

    val utcLabel: JLabel = JLabel("UTC $testCaseNumber")
    val inputLabel: JLabel = JLabel("In")
    val outputLabel: JLabel = JLabel("Out")
    val answerLabel: JLabel = JLabel("Answer")
    val errorLabel: JLabel = JLabel("Cerr")

    init {
        // TestCase 패널 설정
        testCasePanel.apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            border = BorderFactory.createTitledBorder("TestCase $testCaseNumber")
        }

        // utcLabel의 폰트 크기 조정
        utcLabel.apply {
            font = utcLabel.font.deriveFont(utcLabel.font.size2D + 4f)
        }

        // copy 버튼 설정
        inputCopyButton.apply {
            isOpaque = false                            // 배경을 투명하게
            border = BorderFactory.createEmptyBorder()  // 테두리 제거
        }
        outputCopyButton.apply {
            isOpaque = false
            border = BorderFactory.createEmptyBorder()
        }
        answerCopyButton.apply {
            isOpaque = false
            border = BorderFactory.createEmptyBorder()
        }
        errorCopyButton.apply {
            isOpaque = false
            border = BorderFactory.createEmptyBorder()
        }

        // ScrollPane 초기화 및 설정
        inputScrollPane = JBScrollPane(inputTextArea).apply {
            preferredSize = Dimension(120, 60)
            maximumSize = Dimension(Int.MAX_VALUE, 60)
        }
        outputScrollPane = JBScrollPane(outputTextArea).apply {
            preferredSize = Dimension(120, 60)
            maximumSize = Dimension(Int.MAX_VALUE, 60)
        }
        answerScrollPane = JBScrollPane(answerTextArea).apply {
            preferredSize = Dimension(120, 60)
            maximumSize = Dimension(Int.MAX_VALUE, 60)
        }
        errorScrollPane = JBScrollPane(errorTextArea).apply {
            preferredSize = Dimension(120, 60)
            maximumSize = Dimension(Int.MAX_VALUE, 60)
        }

        // TextArea 설정
        inputTextArea.apply {
            lineWrap = true
            wrapStyleWord = true
        }
        outputTextArea.apply {
            lineWrap = true
            wrapStyleWord = true
        }
        answerTextArea.apply {
            isEditable = false
            lineWrap = true
            wrapStyleWord = true
        }
        errorTextArea.apply {
            isEditable = false
            lineWrap = true
            wrapStyleWord = true
        }

        // 라벨 초기화
        inputLabel.text = "In"
        outputLabel.text = "Out"
        answerLabel.text = "Answer"
        errorLabel.text = "Cerr"
    }

    fun removeTestCasePanel(panel: JPanel) {
        testCasePanel.remove(panel)
        SwingUtilities.invokeLater {
            testCasePanel.revalidate()
            testCasePanel.repaint()
        }
    }

    fun updateTestCaseLabel(panel: JPanel, testCaseNumber: Int) {
        val topRowPanel = panel.getComponent(0) as JPanel
        val testCaseLabel = findLabelInPanel(topRowPanel)
        if (testCaseLabel != null) {
            testCaseLabel.text = "UTC $testCaseNumber"
            panel.border = BorderFactory.createTitledBorder("TestCase $testCaseNumber")
        } else {
            println("Warning: No JLabel found in Panel for TestCase $testCaseNumber")
        }
    }

    private fun findLabelInPanel(panel: JPanel): JLabel? {
        for (component in panel.components) {
            if (component is JLabel) {
                return component
            } else if (component is JPanel) {
                val found = findLabelInPanel(component)
                if (found != null) return found
            }
        }
        return null
    }
}
