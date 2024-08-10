package com.github.rungeun.kcj.kotlincodejudge

import com.intellij.openapi.project.Project
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBLabel
import javax.swing.*
import java.awt.*

class MyToolWindowUI(val projectBaseDir: String, val project: Project) {
    val content: JPanel = JPanel()

    private val outerBackgroundColor: JBColor = JBColor.LIGHT_GRAY
    private val innerBackgroundColor: JBColor = JBColor.WHITE

    // TestCaseManager를 초기화하면서 testCasePanel을 전달
    private val testCasePanel = JPanel().apply {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        background = innerBackgroundColor
        isOpaque = true
        border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
        minimumSize = Dimension(200, 300)
    }

    private val testCaseManager = TestCaseManager(testCasePanel)

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
        val runButton = JButton("Run")
        val someRunButton = JButton("Some Run")
        val stopButton = JButton("Stop")

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

        buttonPanel.add(row1Panel)
        buttonPanel.add(row2Panel)

        val scrollPane = JScrollPane(testCasePanel)
        scrollPane.verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        scrollPane.horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        scrollPane.verticalScrollBar.unitIncrement = 16

        val addButtonPanel = JPanel()
        addButtonPanel.layout = BoxLayout(addButtonPanel, BoxLayout.Y_AXIS)
        addButtonPanel.background = innerBackgroundColor
        addButtonPanel.isOpaque = true
        addButtonPanel.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)

        val newTestCasePanel = JPanel()
        newTestCasePanel.layout = BoxLayout(newTestCasePanel, BoxLayout.X_AXIS)
        newTestCasePanel.isOpaque = false
        val addNewTestCaseButton = JButton("New TestCase")

        newTestCasePanel.add(addNewTestCaseButton)

        addNewTestCaseButton.addActionListener {
            testCaseManager.addNewTestCase() // testCaseManager의 addNewTestCase 호출
        }

        addButtonPanel.add(newTestCasePanel)

        fetchButton.addActionListener(FetchTestCaseActionListener(fetchTextField, testCaseManager, fetchLabel))

        runButton.addActionListener {
            TestCaseRunner(projectBaseDir, project).runAllTestCasesSequentially(testCaseManager.getAllTestCaseComponents())
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
    }
}
