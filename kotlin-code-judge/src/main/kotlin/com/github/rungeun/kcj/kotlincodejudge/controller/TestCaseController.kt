// TestCaseController.kt (수정된 부분)
package com.github.rungeun.kcj.kotlincodejudge.controller

import com.github.rungeun.kcj.kotlincodejudge.TestCaseComponents
import com.github.rungeun.kcj.kotlincodejudge.model.TestCaseModel
import com.github.rungeun.kcj.kotlincodejudge.view.TestCasePanelUI
import com.github.rungeun.kcj.kotlincodejudge.TestCaseData
import com.github.rungeun.kcj.kotlincodejudge.UIState
import com.github.rungeun.kcj.kotlincodejudge.UIStateController
import com.intellij.ui.components.JBScrollPane
import java.awt.Dimension
import javax.swing.*

class TestCaseController(
    val model: TestCaseModel,
    private val ui: TestCasePanelUI,
    private val testCaseSaver: TestCaseSaver // TestCaseSaver를 통합하여 사용
) {
    private var isTestCasesLoaded = false

    init {
        // 컨트롤러가 초기화될 때 기존 테스트 케이스를 불러옵니다.
        if (!isTestCasesLoaded) {
            loadTestCases()
            isTestCasesLoaded = true
        }

        // 삭제 버튼에 대한 리스너 추가
        ui.deleteTestCaseButton.addActionListener {
            removeTestCase(ui.testCasePanel)
            saveTestCases() // 삭제 후 테스트 케이스를 저장합니다.
        }
    }

    fun createAndAddTestCasePanel(
        testCaseNumber: Int,
        inputText: String = "입력창",
        outputText: String = "출력창",
        answerLabel: String = "결과창",
        errorLabel: String = "에러창"
    ): TestCaseComponents {
        // 새로운 테스트 케이스 UI 요소 초기화
        val testCasePanelUI = TestCasePanelUI(testCaseNumber)

        // 삭제 버튼에 리스너 등록
        testCasePanelUI.deleteTestCaseButton.addActionListener {
            println("클릭 감지 됨")
            removeTestCase(testCasePanelUI.testCasePanel)
            saveTestCases() // 테스트 케이스 삭제 시 저장
        }

        // 텍스트 영역의 초기값 설정
        testCasePanelUI.inputTextArea.text = inputText
        testCasePanelUI.outputTextArea.text = outputText
        testCasePanelUI.answerTextArea.text = answerLabel
        testCasePanelUI.errorTextArea.text = errorLabel

        // UI 레이아웃 설정
        val testCasePanel = testCasePanelUI.testCasePanel

        // 패널 및 레이블 추가
        val topButtonPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            add(testCasePanelUI.selectTestCase)
            add(testCasePanelUI.utcLabel)
            add(Box.createHorizontalGlue())
            add(testCasePanelUI.uiStateButton)
        }

        val topButtonPanel2 = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            add(Box.createHorizontalGlue())
            add(testCasePanelUI.deleteTestCaseButton)
        }

        // 텍스트 패널 추가
        testCasePanel.add(topButtonPanel)
        testCasePanel.add(topButtonPanel2)
        testCasePanel.add(createTextPanel(testCasePanelUI.inputLabel, testCasePanelUI.inputTextArea, testCasePanelUI.inputCopyButton))
        testCasePanel.add(createTextPanel(testCasePanelUI.outputLabel, testCasePanelUI.outputTextArea, testCasePanelUI.outputCopyButton))
        testCasePanel.add(createTextPanel(testCasePanelUI.answerLabel, testCasePanelUI.answerTextArea, testCasePanelUI.answerCopyButton))
        testCasePanel.add(createTextPanel(testCasePanelUI.errorLabel, testCasePanelUI.errorTextArea, testCasePanelUI.errorCopyButton))

        // UIStateController 초기화 및 초기 상태 설정
        val uiStateController = UIStateController(
            testCasePanel = testCasePanel,
            inputPanel = testCasePanelUI.inputScrollPane.parent as? JPanel ?: JPanel(),
            outputPanel = testCasePanelUI.outputScrollPane.parent as? JPanel ?: JPanel(),
            answerPanel = testCasePanelUI.answerScrollPane.parent as? JPanel ?: JPanel(),
            errorPanel = testCasePanelUI.errorScrollPane.parent as? JPanel ?: JPanel(),
            uiStateButton = testCasePanelUI.uiStateButton
        )
        uiStateController.setState(UIState.UiMidway) // 초기 상태를 UiMidway로 설정

        // TestCaseComponents 객체 생성
        val testCaseComponents = TestCaseComponents(
            panel = testCasePanel,
            selectTestCase = testCasePanelUI.selectTestCase,
            inputTextArea = testCasePanelUI.inputTextArea,
            outputTextArea = testCasePanelUI.outputTextArea,
            answerTextArea = testCasePanelUI.answerTextArea,
            errorTextArea = testCasePanelUI.errorTextArea,
            uiStateController = uiStateController
        )

        // 모델에 새로운 테스트 케이스 컴포넌트를 추가하고 저장
        model.addTestCaseComponent(testCaseComponents)
        saveTestCases() // 새로운 테스트 케이스 생성 시 저장

        return testCaseComponents
    }

    private fun createTextPanel(label: JLabel, textArea: JTextArea, copyButton: JButton): JPanel {
        return JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            add(JPanel().apply {
                layout = BoxLayout(this, BoxLayout.X_AXIS)
                add(label)
                add(Box.createHorizontalGlue())
                add(copyButton)
            })
            add(JBScrollPane(textArea).apply {
                preferredSize = Dimension(120, 60)
                maximumSize = Dimension(Int.MAX_VALUE, 60)
            })
        }
    }

    private fun removeTestCase(panel: JPanel) {
        // model에서 해당 테스트 케이스를 안전하게 제거
        val testCaseComponent = model.getAllTestCaseComponents().find { it.panel == panel }
        if (testCaseComponent != null) {
            model.removeTestCaseComponent(testCaseComponent)

            SwingUtilities.invokeLater {
                panel.parent?.remove(panel)
                panel.parent?.revalidate()
                panel.parent?.repaint()
            }
        } else {
            println("Error: testCaseComponent not found")
        }
    }

    fun selectAllTestCases(select: Boolean) {
        model.getAllTestCaseComponents().forEach { it.selectTestCase.isSelected = select }
    }

    // 현재 테스트 케이스를 파일에 저장
    private fun saveTestCases() {
        val testCases = model.getAllTestCaseComponents().mapIndexed { index, component ->
            TestCaseData(
                testCaseNumber = index + 1,
                input = component.inputTextArea.text.trim(),
                output = component.outputTextArea.text.trim(),
                answer = component.answerTextArea.text.trim(),
                cerr = component.errorTextArea.text.trim(),
                result = getResultFromComponent(component) // 결과 값을 반영
            )
        }
        testCaseSaver.saveTestCases(testCases) // 변환된 리스트를 저장 호출
    }

    // 컴포넌트에서 결과 값을 추출하는 메서드
    private fun getResultFromComponent(component: TestCaseComponents): String {
        val answer = component.answerTextArea.text.trim()
        val output = component.outputTextArea.text.trim()

        return when {
            answer == output -> "AC"  // 정답 일치
            answer.isEmpty() -> "CE"  // 컴파일 오류
            else -> "WA"              // 오답
        }
    }

    fun loadTestCases() {
        if (isTestCasesLoaded) return // 이미 불러왔으면 중복 호출 방지
        clearTestCases() // 기존 테스트 케이스 제거
        isTestCasesLoaded = true // 로드 상태 업데이트

        val loadedTestCases = testCaseSaver.loadTestCases() // 저장된 테스트 케이스 불러오기

        // 불러온 테스트 케이스들을 사용자 UI에 추가
        loadedTestCases.forEach { testCaseData ->
            val testCaseComponent = createAndAddTestCasePanel(
                testCaseNumber = testCaseData.testCaseNumber,
                inputText = testCaseData.input,
                outputText = testCaseData.output,
                answerLabel = testCaseData.answer,
                errorLabel = testCaseData.cerr
            )

            // 사용자 화면에 추가
            ui.testCasePanel.add(testCaseComponent.panel)
            println("Panel added for TestCase: ${testCaseData.testCaseNumber}") // 디버그 로그
        }

        // 모든 테스트 케이스가 추가된 후 UI를 갱신하여 사용자 화면에 반영
        SwingUtilities.invokeLater {
            ui.testCasePanel.revalidate()
            ui.testCasePanel.repaint()
            println("UI updated with loaded test cases.")
        }
    }

    // 기존 clearTestCases 메서드를 수정하여 모든 테스트 케이스 제거 후 리페인트
    fun clearTestCases() {
        val componentsToRemove = model.getAllTestCaseComponents().toList() // 컬렉션 복사하여 안전하게 작업
        componentsToRemove.forEach { component ->
            removeTestCase(component.panel)
        }
        ui.testCasePanel.revalidate()
        ui.testCasePanel.repaint()
    }
}
