package com.github.rungeun.kcj.kotlincodejudge

import com.intellij.ui.components.JBScrollPane
import java.awt.Dimension
import javax.swing.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener

// 'AddTestCase' 버튼의 액션 리스너 클래스
class AddTestCaseActionListener(private val contentPanel: JPanel) : ActionListener {

    override fun actionPerformed(e: ActionEvent) {
        addNewTestCasePanel(contentPanel)
    }

    companion object {
        private var testCaseCount = 1 // 테스트 케이스의 번호를 추적
        private val testCasePanels = mutableListOf<JPanel>() // 테스트 케이스 패널 리스트

        // 입출력창 크기 지정
        private val ioHeight = 60
        private val ioWidth = 120

        fun addNewTestCasePanel(contentPanel: JPanel) {
            // 새로운 테스트 케이스 패널 생성
            val newTestCasePanel = JPanel()
            newTestCasePanel.layout = BoxLayout(newTestCasePanel, BoxLayout.Y_AXIS)
            newTestCasePanel.border = BorderFactory.createTitledBorder("TestCase")

            // 테스트 케이스 패널이 없을 경우 번호를 1로 초기화
            if (testCasePanels.isEmpty()) {
                testCaseCount = 1
            } else {
                testCaseCount = testCasePanels.size + 1
            }

            // 새로운 테스트 케이스 레이아웃 추가
            newTestCasePanel.add(createTestCaseRow1Panel(testCaseCount, newTestCasePanel, contentPanel))
            newTestCasePanel.add(createInputTextPanel())
            newTestCasePanel.add(createOutputTextPanel())
            newTestCasePanel.add(createAnswerTextPanel())
            newTestCasePanel.add(createErrorTextPanel())

            // 생성된 패널을 메인 패널에 추가
            contentPanel.add(newTestCasePanel)
            testCasePanels.add(newTestCasePanel) // 리스트에 추가
            contentPanel.revalidate() // 패널 갱신
            contentPanel.repaint() // 패널 다시 그리기
        }

        // Row 1: 테스트 케이스 라벨, Add 버튼, Delete 버튼
        private fun createTestCaseRow1Panel(testCaseNumber: Int, testCasePanel: JPanel, contentPanel: JPanel): JPanel {
            val testCaseRow1Panel = JPanel()
            testCaseRow1Panel.layout = BoxLayout(testCaseRow1Panel, BoxLayout.X_AXIS)
            testCaseRow1Panel.isOpaque = false
            val testCaseLabel = JLabel("UTC $testCaseNumber") // 번호를 변수로 변경
            val addTestCaseButton = JButton("Copy TestCase")
            val deleteTestCaseButton = JButton("Delete")

            // Delete 버튼에 액션 리스너 추가
            deleteTestCaseButton.addActionListener {
                // 부모 패널에서 해당 테스트 케이스 패널을 제거
                contentPanel.remove(testCasePanel)
                testCasePanels.remove(testCasePanel) // 리스트에서 제거
                renumberTestCases() // 테스트 케이스 재번호 매기기
                contentPanel.revalidate() // 패널 갱신
                contentPanel.repaint() // 패널 다시 그리기

                // 모든 테스트 케이스 패널이 삭제된 경우 번호 초기화
                if (testCasePanels.isEmpty()) {
                    testCaseCount = 1
                }
            }

            testCaseRow1Panel.add(testCaseLabel)
            testCaseRow1Panel.add(Box.createHorizontalGlue()) // 공백 추가
            testCaseRow1Panel.add(addTestCaseButton)
            testCaseRow1Panel.add(deleteTestCaseButton)

            return testCaseRow1Panel
        }

        private fun renumberTestCases() {
            testCasePanels.forEachIndexed { index, panel ->
                val label = (panel.getComponent(0) as JPanel).getComponent(0) as JLabel
                label.text = "UTC ${index + 1}"
            }
            testCaseCount = testCasePanels.size + 1 // 다음 테스트 케이스 번호 동기화
        }

        // Row 2와 3: 입력 라벨, Copy 버튼, 입력 텍스트 영역
        private fun createInputTextPanel(): JPanel {
            val panel = JPanel()
            panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)

            val testCaseRow2Panel = JPanel()
            testCaseRow2Panel.layout = BoxLayout(testCaseRow2Panel, BoxLayout.X_AXIS)
            val inputLabel = JLabel("In")
            val copyInputButton = JButton("Copy")

            testCaseRow2Panel.add(inputLabel)
            testCaseRow2Panel.add(Box.createHorizontalGlue())
            testCaseRow2Panel.add(copyInputButton)

            val inputTextPanel = JPanel()
            inputTextPanel.layout = BoxLayout(inputTextPanel, BoxLayout.X_AXIS)
            inputTextPanel.isOpaque = false

            val inputTextArea = JTextArea(3, 5) // 3줄 높이, 5열 너비의 텍스트 영역
            inputTextArea.lineWrap = true // 자동 줄바꿈 설정
            inputTextArea.wrapStyleWord = true // 단어 단위로 줄바꿈

            val inputScrollPane = JBScrollPane(inputTextArea)
            inputScrollPane.preferredSize = Dimension(ioWidth, ioHeight) // 선호 크기 설정
            inputScrollPane.maximumSize = Dimension(Int.MAX_VALUE, ioHeight) // 최대 크기 설정, 높이는 고정

            inputTextPanel.add(inputScrollPane)
            copyInputButton.addActionListener(CopyTextActionListener(copyInputButton) { inputTextArea.text })

            panel.add(testCaseRow2Panel)
            panel.add(inputTextPanel)

            return panel
        }

        // Row 4와 5: 출력 라벨, Copy 버튼, 출력 텍스트 영역
        private fun createOutputTextPanel(): JPanel {
            val panel = JPanel()
            panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)

            val outputLabelPanel = JPanel()
            outputLabelPanel.layout = BoxLayout(outputLabelPanel, BoxLayout.X_AXIS)
            val outputLabel = JLabel("Out")
            val copyOutputButton = JButton("Copy")

            outputLabelPanel.add(outputLabel)
            outputLabelPanel.add(Box.createHorizontalGlue())
            outputLabelPanel.add(copyOutputButton)

            val outputTextPanel = JPanel()
            outputTextPanel.layout = BoxLayout(outputTextPanel, BoxLayout.X_AXIS)
            outputTextPanel.isOpaque = false

            val outputTextArea = JTextArea(3, 5) // 3줄 높이, 5열 너비의 텍스트 영역
            outputTextArea.lineWrap = true // 자동 줄바꿈 설정
            outputTextArea.wrapStyleWord = true // 단어 단위로 줄바꿈

            val outputScrollPane = JBScrollPane(outputTextArea)
            outputScrollPane.preferredSize = Dimension(ioWidth, ioHeight) // 선호 크기 설정
            outputScrollPane.maximumSize = Dimension(Int.MAX_VALUE, ioHeight) // 최대 크기 설정, 높이는 고정

            outputTextPanel.add(outputScrollPane)
            copyOutputButton.addActionListener(CopyTextActionListener(copyOutputButton) { outputTextArea.text })

            panel.add(outputLabelPanel)
            panel.add(outputTextPanel)

            return panel
        }

        // Row 6와 7: 정답 라벨, Copy 버튼, 정답 텍스트 영역
        private fun createAnswerTextPanel(): JPanel {
            val panel = JPanel()
            panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)

            val answerLabelPanel = JPanel()
            answerLabelPanel.layout = BoxLayout(answerLabelPanel, BoxLayout.X_AXIS)
            val answerLabel = JLabel("Answer")
            val copyAnswerButton = JButton("Copy")

            answerLabelPanel.add(answerLabel)
            answerLabelPanel.add(Box.createHorizontalGlue())
            answerLabelPanel.add(copyAnswerButton)

            val answerTextPanel = JPanel()
            answerTextPanel.layout = BoxLayout(answerTextPanel, BoxLayout.X_AXIS)
            answerTextPanel.isOpaque = false

            val answerTextArea = JTextArea(3, 5) // 3줄 높이, 5열 너비의 텍스트 영역
            answerTextArea.isEditable = false // 편집 불가능하게 설정
            answerTextArea.lineWrap = true // 자동 줄바꿈 설정
            answerTextArea.wrapStyleWord = true // 단어 단위로 줄바꿈
            answerTextArea.text = "TEST1" // 출력 내용 설정
            answerTextArea.dragEnabled = true // 드래그 가능하게 설정

            val answerScrollPane = JBScrollPane(answerTextArea)
            answerScrollPane.preferredSize = Dimension(ioWidth, ioHeight) // 선호 크기 설정
            answerScrollPane.maximumSize = Dimension(Int.MAX_VALUE, ioHeight) // 최대 크기 설정, 높이는 고정

            answerTextPanel.add(answerScrollPane)
            copyAnswerButton.addActionListener(CopyTextActionListener(copyAnswerButton) { answerTextArea.text })

            panel.add(answerLabelPanel)
            panel.add(answerTextPanel)

            return panel
        }

        // Row 8와 9: 오류 라벨, Copy 버튼, 오류 텍스트 영역
        private fun createErrorTextPanel(): JPanel {
            val panel = JPanel()
            panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)

            val errorLabelPanel = JPanel()
            errorLabelPanel.layout = BoxLayout(errorLabelPanel, BoxLayout.X_AXIS)
            val errorLabel = JLabel("Cerr")
            val copyErrorButton = JButton("Copy")

            errorLabelPanel.add(errorLabel)
            errorLabelPanel.add(Box.createHorizontalGlue())
            errorLabelPanel.add(copyErrorButton)

            val errorTextPanel = JPanel()
            errorTextPanel.layout = BoxLayout(errorTextPanel, BoxLayout.X_AXIS)
            errorTextPanel.isOpaque = false

            val errorTextArea = JTextArea(3, 5) // 3줄 높이, 5열 너비의 텍스트 영역
            errorTextArea.isEditable = false // 편집 불가능하게 설정
            errorTextArea.lineWrap = true // 자동 줄바꿈 설정
            errorTextArea.wrapStyleWord = true // 단어 단위로 줄바꿈
            errorTextArea.text = "TEST2" // 출력 내용 설정
            errorTextArea.dragEnabled = true // 드래그 가능하게 설정

            val errorScrollPane = JBScrollPane(errorTextArea)
            errorScrollPane.preferredSize = Dimension(ioWidth, ioHeight) // 선호 크기 설정
            errorScrollPane.maximumSize = Dimension(Int.MAX_VALUE, ioHeight) // 최대 크기 설정, 높이는 고정

            errorTextPanel.add(errorScrollPane)
            copyErrorButton.addActionListener(CopyTextActionListener(copyErrorButton) { errorTextArea.text })

            panel.add(errorLabelPanel)
            panel.add(errorTextPanel)

            return panel
        }
    }
}
