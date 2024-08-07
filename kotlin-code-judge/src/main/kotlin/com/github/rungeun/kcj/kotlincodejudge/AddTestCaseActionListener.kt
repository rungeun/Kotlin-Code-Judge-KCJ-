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
        //입출력창 크기 지정
        private val ioHeight = 60
        private val ioWidth = 120

        fun addNewTestCasePanel(contentPanel: JPanel) {
            // 새로운 테스트 케이스 패널 생성
            val newTestCasePanel = JPanel()
            newTestCasePanel.layout = BoxLayout(newTestCasePanel, BoxLayout.Y_AXIS)
            newTestCasePanel.border = BorderFactory.createTitledBorder("TestCase")

            // 새로운 테스트 케이스 레이아웃 추가
            newTestCasePanel.add(createTestCaseRow1Panel(testCaseCount))
            newTestCasePanel.add(createTestCaseRow2Panel())
            newTestCasePanel.add(createInputTextPanel())
            newTestCasePanel.add(createOutputLabelPanel())
            newTestCasePanel.add(createOutputTextPanel())
            newTestCasePanel.add(createAnswerLabelPanel())
            newTestCasePanel.add(createAnswerTextPanel())
            newTestCasePanel.add(createErrorLabelPanel())
            newTestCasePanel.add(createErrorTextPanel())

            // 생성된 패널을 메인 패널에 추가
            contentPanel.add(newTestCasePanel)
            contentPanel.revalidate() // 패널 갱신
            contentPanel.repaint() // 패널 다시 그리기

            testCaseCount++// 테스트 케이스 번호 증가
        }

        // Row 1: 테스트 케이스 라벨, Add 버튼, Delete 버튼
        private fun createTestCaseRow1Panel(testCaseNumber: Int): JPanel {
            val testCaseRow1Panel = JPanel()
            testCaseRow1Panel.layout = BoxLayout(testCaseRow1Panel, BoxLayout.X_AXIS)
            testCaseRow1Panel.isOpaque = false
            val testCaseLabel = JLabel("UTC $testCaseNumber") // TODO: 번호를 변수로 바꾸기
            val addTestCaseButton = JButton("Copy TestCase")
            val deleteTestCaseButton = JButton("Delete")

            testCaseRow1Panel.add(testCaseLabel)
            testCaseRow1Panel.add(Box.createHorizontalGlue()) // 공백 추가
            testCaseRow1Panel.add(addTestCaseButton)
            testCaseRow1Panel.add(deleteTestCaseButton)

            return testCaseRow1Panel
        }

        // Row 2: 입력 라벨, Copy 버튼
        private fun createTestCaseRow2Panel(): JPanel {
            val testCaseRow2Panel = JPanel()
            testCaseRow2Panel.layout = BoxLayout(testCaseRow2Panel, BoxLayout.X_AXIS)
            val inputLabel = JLabel("In")
            val copyInputButton = JButton("Copy")

            testCaseRow2Panel.add(inputLabel)
            testCaseRow2Panel.add(Box.createHorizontalGlue())
            testCaseRow2Panel.add(copyInputButton)

            return testCaseRow2Panel
        }

        // Row 3: 입력 텍스트 영역
        private fun createInputTextPanel(): JPanel {
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

            return inputTextPanel
        }

        // Row 4: 출력 라벨, Copy 버튼
        private fun createOutputLabelPanel(): JPanel {
            val outputLabelPanel = JPanel()
            outputLabelPanel.layout = BoxLayout(outputLabelPanel, BoxLayout.X_AXIS)
            val outputLabel = JLabel("Out")
            val copyOutputButton = JButton("Copy")

            outputLabelPanel.add(outputLabel)
            outputLabelPanel.add(Box.createHorizontalGlue())
            outputLabelPanel.add(copyOutputButton)

            return outputLabelPanel
        }

        // Row 5: 출력 텍스트 영역
        private fun createOutputTextPanel(): JPanel {
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

            return outputTextPanel
        }

        // Row 6: 정답 라벨, Copy 버튼
        private fun createAnswerLabelPanel(): JPanel {
            val answerLabelPanel = JPanel()
            answerLabelPanel.layout = BoxLayout(answerLabelPanel, BoxLayout.X_AXIS)
            val answerLabel = JLabel("Answer")
            val copyAnswerButton = JButton("Copy")

            answerLabelPanel.add(answerLabel)
            answerLabelPanel.add(Box.createHorizontalGlue())
            answerLabelPanel.add(copyAnswerButton)

            return answerLabelPanel
        }

        // Row 7: 정답 텍스트 영역
        private fun createAnswerTextPanel(): JPanel {
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

            return answerTextPanel
        }

        // Row 8: 오류 라벨, Copy 버튼
        private fun createErrorLabelPanel(): JPanel {
            val errorLabelPanel = JPanel()
            errorLabelPanel.layout = BoxLayout(errorLabelPanel, BoxLayout.X_AXIS)
            val errorLabel = JLabel("Cerr")
            val copyErrorButton = JButton("Copy")

            errorLabelPanel.add(errorLabel)
            errorLabelPanel.add(Box.createHorizontalGlue())
            errorLabelPanel.add(copyErrorButton)

            return errorLabelPanel
        }

        // Row 9: 오류 텍스트 영역
        private fun createErrorTextPanel(): JPanel {
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

            return errorTextPanel
        }
    }
}
