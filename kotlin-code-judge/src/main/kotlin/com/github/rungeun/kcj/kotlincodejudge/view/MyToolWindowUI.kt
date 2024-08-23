package com.github.rungeun.kcj.kotlincodejudge.view

import com.github.rungeun.kcj.kotlincodejudge.*
import com.github.rungeun.kcj.kotlincodejudge.controller.GiveCoffeeActionListener
import com.github.rungeun.kcj.kotlincodejudge.controller.GuideActionListener
import com.intellij.openapi.project.Project
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBScrollPane
import javax.swing.*
import java.awt.*
import javax.swing.border.TitledBorder

class MyToolWindowUI(val projectBaseDir: String, val project: Project) {
    val content: JPanel = JPanel()

    private val outerBackgroundColor: JBColor = JBColor.GREEN
    private val innerBackgroundColor: JBColor = JBColor.WHITE

    private val testCasePanel = JPanel().apply {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        background = innerBackgroundColor
        isOpaque = true
        border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
        minimumSize = Dimension(200, 300)
    }

    private val testCaseManager = TestCaseManager(testCasePanel)

    private lateinit var testCaseRunner: TestCaseRunner
    private lateinit var runButton: JButton
    private lateinit var someRunButton: JButton
    private lateinit var stopButton: JButton
    private lateinit var newTestCaseButton: JButton

    init {
        content.layout = BoxLayout(content, BoxLayout.Y_AXIS)
        content.background = outerBackgroundColor

        val fetchPanel = JPanel()
        fetchPanel.layout = BoxLayout(fetchPanel, BoxLayout.X_AXIS)
        fetchPanel.background = innerBackgroundColor
        fetchPanel.isOpaque = true
        fetchPanel.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)

        val fetchLabel = JBLabel("ProblemNumber: ")
        val fetchTextField = JTextField(7)
        fetchTextField.preferredSize = Dimension(60, 30)
        fetchTextField.maximumSize = Dimension(70, 30)
        val fetchButton = JButton("Fetch Test Cases")

        fetchPanel.add(fetchLabel)
        fetchPanel.add(fetchTextField)
        fetchPanel.add(fetchButton)

        val buttonPanel = JPanel()
        buttonPanel.layout = BoxLayout(buttonPanel, BoxLayout.Y_AXIS)
        buttonPanel.background = innerBackgroundColor
        buttonPanel.isOpaque = true
        buttonPanel.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)

        val row1Panel = JPanel()
        row1Panel.layout = BoxLayout(row1Panel, BoxLayout.X_AXIS)
        row1Panel.isOpaque = false
        runButton = JButton("Run")
        someRunButton = JButton("Some Run")
        stopButton = JButton("Stop")

        row1Panel.add(runButton)
        row1Panel.add(someRunButton)
        row1Panel.add(stopButton)

        val row2Panel = JPanel()
        row2Panel.layout = BoxLayout(row2Panel, BoxLayout.X_AXIS)
        row2Panel.isOpaque = false
        val donateButton = JButton("Give coffee :>")
        val guideButton = JButton("Guide")
        donateButton.addActionListener(GiveCoffeeActionListener())
        guideButton.addActionListener(GuideActionListener())

        row2Panel.add(donateButton)
        row2Panel.add(guideButton)

        val row3Panel = JPanel()
        row3Panel.layout = BoxLayout(row3Panel, BoxLayout.X_AXIS)
        row3Panel.isOpaque = false
        val selectAll = JButton("All")
        val clearSelection = JButton("Clear")

        row3Panel.add(selectAll)
        row3Panel.add(clearSelection)

        buttonPanel.add(row1Panel)
        buttonPanel.add(row2Panel)
        buttonPanel.add(row3Panel)

        val scrollPane = JBScrollPane(testCasePanel)
        scrollPane.verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        scrollPane.horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        scrollPane.verticalScrollBar.unitIncrement = 16

        val addButtonPanel = JPanel()
        addButtonPanel.layout = BoxLayout(addButtonPanel, BoxLayout.Y_AXIS)
        addButtonPanel.background = innerBackgroundColor
        addButtonPanel.isOpaque = true
        addButtonPanel.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)

        newTestCaseButton = JButton("New TestCase")
        val newTestCasePanel = JPanel()
        newTestCasePanel.layout = BoxLayout(newTestCasePanel, BoxLayout.X_AXIS)
        newTestCasePanel.isOpaque = false
        newTestCasePanel.add(newTestCaseButton)


        // 'All' 버튼 클릭 시 모든 체크박스 선택
        selectAll.addActionListener {
            testCaseManager.selectAllTestCases(true)
        }

        // 'Clear' 버튼 클릭 시 모든 체크박스 해제
        clearSelection.addActionListener {
            testCaseManager.selectAllTestCases(false)
        }

        newTestCaseButton.addActionListener {
            testCaseManager.addNewTestCase()
        }

        addButtonPanel.add(newTestCasePanel)

        fetchButton.addActionListener(FetchTestCaseActionListener(fetchTextField, testCaseManager, fetchLabel))

        testCaseRunner = TestCaseRunner(projectBaseDir, project,
            onExecutionFinished = {
                runButton.isEnabled = true
                someRunButton.isEnabled = true
                newTestCaseButton.isEnabled = true
                stopButton.isEnabled = false
            },
            onTestCaseFinished = { utcNumber, result ->
                onTestCaseFinished(result, utcNumber)
            }
        )

        runButton.addActionListener {
            disableButtonsDuringExecution()
            testCaseRunner.runAllTestCasesSequentially(testCaseManager.getAllTestCaseComponents())
        }

        someRunButton.addActionListener {
            disableButtonsDuringExecution()
            testCaseRunner.runSelectedTestCasesSequentially(testCaseManager.getSelectedTestCaseComponents())
        }

        stopButton.addActionListener {
            stopButton.isEnabled = false
            testCaseRunner.requestStop()
            testCaseManager.getRunningTestCase()?.let {
                SwingUtilities.invokeLater {
                    val border = it.panel.border
                    if (border is TitledBorder) {
                        it.panel.border = BorderFactory.createTitledBorder("Stopping...")
                    }
                }
            }
        }

        content.add(Box.createVerticalStrut(10))
        content.add(fetchPanel)
        content.add(Box.createVerticalStrut(10))
        content.add(buttonPanel)
        content.add(Box.createVerticalStrut(10))
        content.add(scrollPane)
        content.add(addButtonPanel)
        content.add(Box.createVerticalStrut(10))
        content.isVisible = true

        stopButton.isEnabled = false
    }

    private fun disableButtonsDuringExecution() {
        runButton.isEnabled = false
        someRunButton.isEnabled = false
        newTestCaseButton.isEnabled = false
        stopButton.isEnabled = true
    }

    private fun onTestCaseFinished(result: String, utcNumber: Int) {
        when (result) {
            "AC" -> testCaseManager.setUiStateForTestCase(utcNumber, UIState.UiFolded)
            else -> testCaseManager.setUiStateForTestCase(utcNumber, UIState.UiExpanded)
        }
    }
}
