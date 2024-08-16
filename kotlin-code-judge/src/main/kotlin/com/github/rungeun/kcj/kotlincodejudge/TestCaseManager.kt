package com.github.rungeun.kcj.kotlincodejudge

import com.intellij.ui.components.JBScrollPane
import java.awt.Color
import java.awt.Dimension
import javax.swing.*
import javax.swing.border.TitledBorder
class TestCaseManager(private val testCasePanel: JPanel) {

    private var testCaseCount = 1
    private val testCasePanels = mutableListOf<TestCaseComponents>()
    private var runningTestCase: TestCaseComponents? = null

    fun addTestCaseComponent(testCaseComponent: TestCaseComponents) {
        testCasePanels.add(testCaseComponent)
    }

    fun removeTestCaseComponent(testCaseComponent: TestCaseComponents) {
        testCasePanels.remove(testCaseComponent)
    }

    fun getAllTestCaseComponents(): List<TestCaseComponents> {
        return testCasePanels
    }

    fun getSelectedTestCaseComponents(): List<TestCaseComponents> {
        return testCasePanels.filter { it.selectTestCase.isSelected }
    }

    fun setRunningTestCase(testCaseComponent: TestCaseComponents) {
        runningTestCase = testCaseComponent
    }

    fun getRunningTestCase(): TestCaseComponents? {
        return getAllTestCaseComponents().find {
            val border = it.panel.border
            border is TitledBorder && border.title.startsWith("Judging")
        }
    }

    fun addNewTestCase(inputText: String = "", outputText: String = "") {
        val utcNumber = testCaseCount
        val newTestCasePanel = JPanel()
        newTestCasePanel.layout = BoxLayout(newTestCasePanel, BoxLayout.Y_AXIS)
        newTestCasePanel.border = BorderFactory.createTitledBorder("TestCase $utcNumber")

        val inputTextArea = JTextArea(3, 5)
        val outputTextArea = JTextArea(3, 5)
        val answerTextArea = JTextArea(3, 5)
        val errorTextArea = JTextArea(3, 5)
        val selectTestCase = JCheckBox()

        newTestCasePanel.add(createTestCaseRow1Panel(utcNumber, newTestCasePanel, selectTestCase))
        newTestCasePanel.add(createInputTextPanel(inputTextArea, inputText))
        newTestCasePanel.add(createOutputTextPanel(outputTextArea, outputText))
        newTestCasePanel.add(createAnswerTextPanel(answerTextArea))
        newTestCasePanel.add(createErrorTextPanel(errorTextArea))

        testCasePanel.add(newTestCasePanel)
        testCasePanels.add(
            TestCaseComponents(
                newTestCasePanel,
                selectTestCase,
                inputTextArea,
                outputTextArea,
                answerTextArea,
                errorTextArea
            )
        )
        testCasePanel.revalidate()
        testCasePanel.repaint()

        testCaseCount++
        renumberTestCases() // 새로운 테스트 케이스 추가 후 리넘버링
    }

    private fun createTestCaseRow1Panel(testCaseNumber: Int, testCasePanel: JPanel, checkBox: JCheckBox): JPanel {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.X_AXIS)
        panel.isOpaque = false
        val testCaseLabel = JLabel("UTC $testCaseNumber")
        val copyTestCaseButton = JButton("Copy TestCase")
        val deleteTestCaseButton = JButton("Delete")

        deleteTestCaseButton.addActionListener {
            removeTestCaseComponentByPanel(testCasePanel)
            renumberTestCases()
        }







        panel.add(checkBox)
        panel.add(testCaseLabel)
        panel.add(Box.createHorizontalGlue())
        panel.add(copyTestCaseButton)
        panel.add(deleteTestCaseButton)

        return panel
    }

    private fun createInputTextPanel(textArea: JTextArea, inputText: String): JPanel {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)

        val labelPanel = JPanel()
        labelPanel.layout = BoxLayout(labelPanel, BoxLayout.X_AXIS)
        val inputLabel = JLabel("In")
        val copyButton = JButton("Copy")
        copyButton.isOpaque = false
        copyButton.background = Color(0, 0, 0, 0) // 투명한 배경 설정
        copyButton.border = BorderFactory.createEmptyBorder() // 경계선을 없앰

        labelPanel.add(inputLabel)
        labelPanel.add(Box.createHorizontalGlue())
        labelPanel.add(copyButton)
        copyButton.addActionListener(CopyTextActionListener(copyButton) { textArea.text })

        textArea.text = inputText
        val scrollPane = JBScrollPane(textArea)
        scrollPane.preferredSize = Dimension(120, 60)
        scrollPane.maximumSize = Dimension(Int.MAX_VALUE, 60)

        panel.add(labelPanel)
        panel.add(scrollPane)

        return panel
    }

    private fun createOutputTextPanel(textArea: JTextArea, outputText: String): JPanel {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)

        val labelPanel = JPanel()
        labelPanel.layout = BoxLayout(labelPanel, BoxLayout.X_AXIS)
        val outputLabel = JLabel("Out")
        val copyButton = JButton("Copy")
        copyButton.isOpaque = false
        copyButton.background = Color(0, 0, 0, 0)
        copyButton.border = BorderFactory.createEmptyBorder()
        copyButton.addActionListener(CopyTextActionListener(copyButton) { textArea.text })

        labelPanel.add(outputLabel)
        labelPanel.add(Box.createHorizontalGlue())
        labelPanel.add(copyButton)

        textArea.text = outputText
        val scrollPane = JBScrollPane(textArea)
        scrollPane.preferredSize = Dimension(120, 60)
        scrollPane.maximumSize = Dimension(Int.MAX_VALUE, 60)

        panel.add(labelPanel)
        panel.add(scrollPane)

        return panel
    }

    private fun createAnswerTextPanel(textArea: JTextArea): JPanel {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)

        val labelPanel = JPanel()
        labelPanel.layout = BoxLayout(labelPanel, BoxLayout.X_AXIS)
        val answerLabel = JLabel("Answer")
        val copyButton = JButton("Copy")
        copyButton.isOpaque = false
        copyButton.background = Color(0, 0, 0, 0)
        copyButton.border = BorderFactory.createEmptyBorder()
        copyButton.addActionListener(CopyTextActionListener(copyButton) { textArea.text })

        labelPanel.add(answerLabel)
        labelPanel.add(Box.createHorizontalGlue())
        labelPanel.add(copyButton)

        val scrollPane = JBScrollPane(textArea)
        scrollPane.preferredSize = Dimension(120, 60)
        scrollPane.maximumSize = Dimension(Int.MAX_VALUE, 60)

        panel.add(labelPanel)
        panel.add(scrollPane)

        return panel
    }

    private fun createErrorTextPanel(textArea: JTextArea): JPanel {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)

        val labelPanel = JPanel()
        labelPanel.layout = BoxLayout(labelPanel, BoxLayout.X_AXIS)
        val errorLabel = JLabel("Cerr")
        val copyButton = JButton("Copy")
        copyButton.isOpaque = false
        copyButton.background = Color(0, 0, 0, 0)
        copyButton.border = BorderFactory.createEmptyBorder()
        copyButton.addActionListener(CopyTextActionListener(copyButton) { textArea.text })

        labelPanel.add(errorLabel)
        labelPanel.add(Box.createHorizontalGlue())
        labelPanel.add(copyButton)

        val scrollPane = JBScrollPane(textArea)
        scrollPane.preferredSize = Dimension(120, 60)
        scrollPane.maximumSize = Dimension(Int.MAX_VALUE, 60)

        panel.add(labelPanel)
        panel.add(scrollPane)

        return panel
    }

    private fun removeTestCaseComponentByPanel(panel: JPanel) {
        testCasePanels.removeIf { it.panel == panel }
        testCasePanel.remove(panel)

        // Swing의 이벤트 디스패치 스레드에서 레이아웃을 강제로 재계산
        SwingUtilities.invokeLater {
            testCasePanel.revalidate()
            testCasePanel.repaint()
        }
    }


    private fun renumberTestCases() {
        testCasePanels.forEachIndexed { index, testCase ->
            val labelPanel = testCase.panel.getComponent(0) as JPanel
            val label = labelPanel.getComponent(1) as JLabel // UTC 번호 라벨이 있는 위치를 정확히 지정
            label.text = "UTC ${index + 1}"
            testCase.panel.border = BorderFactory.createTitledBorder("TestCase ${index + 1}")
        }
        testCaseCount = testCasePanels.size + 1
    }

    fun selectAllTestCases(selected: Boolean) {
        testCasePanels.forEach { it.selectTestCase.isSelected = selected }
    }

    fun getSelectedTestCases(): List<TestCaseComponents> {
        return testCasePanels.filter { it.selectTestCase.isSelected }
    }
    fun getTestCases(): List<TestCaseComponents> {
        return testCasePanels
    }
}

