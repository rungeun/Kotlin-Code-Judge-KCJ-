package com.github.rungeun.kcj.kotlincodejudge
import com.intellij.ui.JBColor
import javax.swing.*
import java.awt.*

class MyToolWindowUI {
    val content: JPanel = JPanel()

    private val outerBackgroundColor: JBColor = JBColor.GREEN
    private val innerBackgroundColor: JBColor = JBColor.WHITE

    init {
        // 메인 레이아웃 설정
        content.layout = BoxLayout(content, BoxLayout.Y_AXIS)
        content.background = outerBackgroundColor

        // 기능 버튼 영역
        val buttonPanel = JPanel()
        buttonPanel.layout = BoxLayout(buttonPanel, BoxLayout.Y_AXIS)
        buttonPanel.background = innerBackgroundColor
        buttonPanel.isOpaque = true
        buttonPanel.border = BorderFactory.createEmptyBorder(10, 10, 10, 10) // 패널 내부 패딩 추가

        // 1행: 기능 버튼들
        val row1Panel = JPanel()
        row1Panel.layout = BoxLayout(row1Panel, BoxLayout.X_AXIS)
        row1Panel.isOpaque = false
        val runButton = JButton("Run")
        val someRunButton = JButton("Some Run")
        val stopButton = JButton("Stop")

        row1Panel.add(runButton)
        row1Panel.add(someRunButton)
        row1Panel.add(stopButton)

        // 2행: 기능 버튼들
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

        // 테스트 케이스 영역
        val testCasePanel = JPanel()
        testCasePanel.layout = BoxLayout(testCasePanel, BoxLayout.Y_AXIS)
        testCasePanel.background = innerBackgroundColor
        testCasePanel.isOpaque = true
        testCasePanel.border = BorderFactory.createEmptyBorder(10, 10, 10, 10) // 패널 내부 패딩 추가
        testCasePanel.minimumSize = Dimension(200, 300) // 최소 크기 설정

        // JScrollPane을 사용하여 스크롤 가능하게 설정
        val scrollPane = JScrollPane(testCasePanel)
        scrollPane.verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        scrollPane.horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        scrollPane.verticalScrollBar.unitIncrement = 16 // 스크롤 속도 조절

        // Add button 패널
        val addButtonPanel = JPanel()
        addButtonPanel.layout = BoxLayout(addButtonPanel, BoxLayout.Y_AXIS)
        addButtonPanel.background = innerBackgroundColor
        addButtonPanel.isOpaque = true
        addButtonPanel.border = BorderFactory.createEmptyBorder(10, 10, 10, 10) // 패널 내부 패딩 추가

        // 1행: 새로운 테스트 케이스 추가 버튼
        val newTestCasePanel = JPanel()
        newTestCasePanel.layout = BoxLayout(newTestCasePanel, BoxLayout.X_AXIS)
        newTestCasePanel.isOpaque = false
        val addNewTestCaseButton = JButton("New TestCase")

        newTestCasePanel.add(addNewTestCaseButton)


        addNewTestCaseButton.addActionListener {
            AddTestCaseActionListener.addNewTestCasePanel(testCasePanel)
        }

        addButtonPanel.add(newTestCasePanel)

        // 메인 콘텐츠 패널에 컴포넌트 추가
        content.add(Box.createVerticalStrut(10)) // Add top margin
        content.add(buttonPanel)
        content.add(Box.createVerticalStrut(10)) // Add margin between button panel and test case panel
        //content.add(testCasePanel)
        content.add(scrollPane)
        content.add(addButtonPanel)
        content.add(Box.createVerticalStrut(10)) // Add bottom margin

        content.isVisible = true
    }
}
