// MainController.kt
package com.github.rungeun.kcj.kotlincodejudge.view

import com.github.rungeun.kcj.kotlincodejudge.Colors
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBScrollPane
import javax.swing.*
import java.awt.*

class MainToolWindowUI {
    // UI Components (호출 받는 부분)
    val content: JPanel = JPanel()

    private  val filePanel = JPanel()
    internal val fileLabel = JBLabel("Current File: ")
    // 연동 영역
    private  val fetchPanel = JPanel()
    internal val fetchLabel = JBLabel("ProblemNumber: ")
    internal val fetchTextField = JTextField(7)
    internal val fetchButton = JButton("Fetch Test Cases")

    // 상단 기능 버튼 영역
    internal val topButtonPanel = JPanel()

    // +1행
    private val topButtonRow1Panel = JPanel()
    internal val runButton = JButton("Run")
    internal val someRunButton = JButton("Some Run")
    internal val stopButton = JButton("Stop")

    // +2행
    private val topButtonRow2Panel = JPanel()
    internal val donateButton = JButton("Give coffee :>")
    internal val guideButton = JButton("Guide")

    // +3행
    private val topButtonRow3Panel = JPanel()
    internal val selectAll = JButton("All")
    internal val clearSelection = JButton("Clear")

    // 테스트 케이스 영역
    val testCasePanel = JPanel()
    private val testCaseScrollPanel = JBScrollPane(testCasePanel)

    // 테스트 케이스 추가 버튼 영역
    private val newTestCaseButtonPanel = JPanel()
    internal val newTestCaseButton = JButton("New TestCase")

    init {
        // UI Components
        content.apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            background = Colors.outerBackground
        }

        // 현재 파일명
        filePanel.apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            background = Colors.innerBackground
            isOpaque = true
            border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
            add(fileLabel)
            add(Box.createHorizontalGlue())
        }

        // 연동 영역
        fetchTextField.apply {
            preferredSize = Dimension(60, 30)
            maximumSize = Dimension(70, 30)
        }

        fetchPanel.apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            background = Colors.innerBackground
            isOpaque = true
            border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
            add(fetchLabel)
            add(fetchTextField)
            add(fetchButton)
        }

        // 상단 기능 버튼 영역
        stopButton.apply {
            isEnabled = false
        }

        topButtonRow1Panel.apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            isOpaque = false
            add(runButton)
            add(someRunButton)
            add(stopButton)
        }

        topButtonRow2Panel.apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            isOpaque = false
            add(donateButton)
            add(guideButton)
        }

        topButtonRow3Panel.apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            isOpaque = false
            add(selectAll)
            add(clearSelection)
        }

        topButtonPanel.apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            background = Colors.innerBackground
            isOpaque = true
            border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
            add(topButtonRow1Panel)
            add(topButtonRow2Panel)
            add(topButtonRow3Panel)
        }

        // testCaseScrollPane 영역
        testCasePanel.apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            background = Colors.innerBackground
            isOpaque = true
            border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
            minimumSize = Dimension(200, 300)
        }

        testCaseScrollPanel.apply {
            verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
            horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
            verticalScrollBar.unitIncrement = 16
        }

        // newTestCaseButton 영역
        newTestCaseButtonPanel.apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            background = Colors.innerBackground
            isOpaque = true
            border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
            add(JPanel().apply {
                layout = BoxLayout(this, BoxLayout.X_AXIS)
                isOpaque = false
                add(newTestCaseButton)
            })
        }

        // Adding components to the content panel
        content.apply {
            add(Box.createVerticalStrut(10))
            add(filePanel)
            add(Box.createVerticalStrut(10))
            add(fetchPanel)
            add(Box.createVerticalStrut(10))
            add(topButtonPanel)
            add(Box.createVerticalStrut(10))
            add(testCaseScrollPanel)
            add(newTestCaseButtonPanel)
            add(Box.createVerticalStrut(10))
        }
    }


    // View 내부에서 testCasePanel을 관리하는 메서드 추가
    fun addTestCasePanel(panel: JPanel) {
        testCasePanel.add(panel)
        testCasePanel.revalidate()
        testCasePanel.repaint()
    }

    fun updateFileLabel(fileName: String?) {
        fileLabel.text = ":Current File: ${fileName ?: "No file selected"}"
    }

    fun removeTestCasePanel(panel: JPanel) {
        testCasePanel.remove(panel)
        testCasePanel.revalidate()
        testCasePanel.repaint()
    }

}