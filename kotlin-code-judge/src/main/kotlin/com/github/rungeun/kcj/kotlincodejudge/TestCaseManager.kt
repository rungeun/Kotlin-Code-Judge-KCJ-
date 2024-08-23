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
    private val uiStateManagers = mutableMapOf<Int, UIStateManager>()
    private val testCaseComponentsList = mutableListOf<TestCaseComponents>()

    fun addTestCases(testCases: List<TestCase>) {
        testCases.forEach { testCase ->
            addNewTestCase(testCase.input, testCase.output) // 필요한 경우 다른 필드도 추가
        }
    }
    fun clearAllTestCases() {
        // 패널에서 모든 테스트 케이스 UI 제거
        testCasePanel.removeAll()
        // 리스트에서 모든 테스트 케이스 제거
        testCaseComponentsList.clear()
        // UI 갱신
        testCasePanel.revalidate()
        testCasePanel.repaint()
    }

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
        val uiStateButton = JButton("UI state changes")

        val inputPanel = createInputTextPanel(inputTextArea, inputText)
        val outputPanel = createOutputTextPanel(outputTextArea, outputText)
        val answerPanel = createAnswerTextPanel(answerTextArea)
        val errorPanel = createErrorTextPanel(errorTextArea)

        newTestCasePanel.add(createTestCaseRow1Panel(utcNumber, newTestCasePanel, selectTestCase, uiStateButton))
        newTestCasePanel.add(inputPanel)
        newTestCasePanel.add(outputPanel)
        newTestCasePanel.add(answerPanel)
        newTestCasePanel.add(errorPanel)

        val uiStateManager = UIStateManager(
            newTestCasePanel,
            inputPanel,
            outputPanel,
            answerPanel,
            errorPanel,
            uiStateButton
        )

        val testCaseComponents = TestCaseComponents(
            newTestCasePanel,
            selectTestCase,
            inputTextArea,
            outputTextArea,
            answerTextArea,
            errorTextArea,
            uiStateManager
        )

        testCasePanels.add(testCaseComponents)

        testCasePanel.add(newTestCasePanel)
        testCasePanel.revalidate()
        testCasePanel.repaint()

        testCaseCount++
        renumberTestCases()
    }

    private fun createTestCaseRow1Panel(
        testCaseNumber: Int,
        testCasePanel: JPanel,
        checkBox: JCheckBox,
        uiStateButton: JButton
    ): JPanel {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
        panel.isOpaque = false

        val topRowPanel = JPanel()
        topRowPanel.layout = BoxLayout(topRowPanel, BoxLayout.X_AXIS)
        topRowPanel.isOpaque = false

        val testCaseLabel = JLabel("UTC $testCaseNumber")

        val bottomRowPanel = JPanel()
        bottomRowPanel.layout = BoxLayout(bottomRowPanel, BoxLayout.X_AXIS)
        bottomRowPanel.isOpaque = false

        val deleteTestCaseButton = JButton("Delete")

        deleteTestCaseButton.addActionListener {
            removeTestCaseComponentByPanel(testCasePanel)
            renumberTestCases()
        }

        topRowPanel.add(checkBox)
        topRowPanel.add(testCaseLabel)
        topRowPanel.add(Box.createHorizontalGlue())
        topRowPanel.add(uiStateButton)

        bottomRowPanel.add(Box.createHorizontalGlue())
        bottomRowPanel.add(deleteTestCaseButton)

        panel.add(topRowPanel)
        panel.add(bottomRowPanel)

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
        copyButton.background = Color(0, 0, 0, 0)
        copyButton.border = BorderFactory.createEmptyBorder()

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
            val topRowPanel = testCase.panel.getComponent(0) as JPanel

            // topRowPanel 내부에서 JLabel을 찾기 위해 모든 자식 컴포넌트를 재귀적으로 탐색
            val testCaseLabel = findLabelInPanel(topRowPanel)
            if (testCaseLabel != null) {
                testCaseLabel.text = "UTC ${index + 1}"
            } else {
                println("Warning: No JLabel found in topRowPanel for TestCase ${index + 1}")
            }

            // 패널의 테두리도 업데이트
            testCase.panel.border = BorderFactory.createTitledBorder("TestCase ${index + 1}")
        }

        // 다음에 생성될 테스트 케이스 번호 동기화
        testCaseCount = testCasePanels.size + 1
    }

    // JPanel 내부에서 JLabel을 재귀적으로 찾는 함수
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
    fun setUiStateForTestCase(utcNumber: Int, state: UIState) {
        if (utcNumber < 0 || utcNumber >= testCasePanels.size) {
            println("Error: Invalid test case number: $utcNumber")
            return
        }

        uiStateManagers[utcNumber]?.setState(state)
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

    fun updateTestCaseResult(utcNumber: Int, result: String) {
        val testCaseComponent = testCasePanels.getOrNull(utcNumber - 1)
        if (testCaseComponent != null) {
            testCaseComponent.uiStateManager.updateResult(result)
            testCaseComponent.uiStateManager.setState(UIState.UiExpanded, executed = true)
        } else {
            println("Error: TestCase $utcNumber not found")
        }
    }

}
