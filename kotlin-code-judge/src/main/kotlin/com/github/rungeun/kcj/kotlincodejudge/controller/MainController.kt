package com.github.rungeun.kcj.kotlincodejudge.controller

import com.github.rungeun.kcj.kotlincodejudge.TestCaseComponents
import com.github.rungeun.kcj.kotlincodejudge.TestCaseData
import com.github.rungeun.kcj.kotlincodejudge.model.FetchTestCaseModel
import com.github.rungeun.kcj.kotlincodejudge.model.TestCaseModel
import com.github.rungeun.kcj.kotlincodejudge.view.MainToolWindowUI
import com.github.rungeun.kcj.kotlincodejudge.view.TestCasePanelUI
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import javax.swing.BorderFactory
import javax.swing.SwingUtilities
import javax.swing.border.TitledBorder

class MainController(private val ui: MainToolWindowUI, project: Project) {
    private val model = TestCaseModel()
    private val fileTracker = FileTracker(project)
    private val testCaseSaver = TestCaseSaver(fileTracker)  // TestCaseSaver 인스턴스 생성
    private var testCaseController: TestCaseController

    private val fetchTestCaseModel = FetchTestCaseModel()
    private val fetchTestCaseController: FetchTestCaseController

    private val testCaseRunnerController = TestCaseRunnerController(model, project.basePath ?: "", project)

    private var isRunning: Boolean = false

    init {
        val connection = project.messageBus.connect()
        connection.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, object : FileEditorManagerListener {
            override fun selectionChanged(event: FileEditorManagerEvent) {
                updateCurrentFile(event.newFile)
            }
        })

        // 초기 파일 설정
        updateCurrentFile(FileEditorManager.getInstance(project).selectedFiles.firstOrNull())

        // TestCaseController 인스턴스 생성 시 testCaseSaver 추가
        val initialTestCasePanelUI = TestCasePanelUI(0)
        testCaseController = TestCaseController(model, initialTestCasePanelUI, testCaseSaver)
        fetchTestCaseController = FetchTestCaseController(fetchTestCaseModel, ui, testCaseController)

        // 초기 버튼 상태 설정
        updateButtonStates(isRunning = false)

        ui.runButton.addActionListener {
            if (!isRunning) {  // 실행 중이 아닐 때만 실행
                println("start: Run")
                updateButtonStates(isRunning = true)
                val testCasePanels = model.getAllTestCaseComponents()
                testCaseRunnerController.runAllTestCasesSequentially(testCasePanels) {
                    updateButtonStates(isRunning = false)
                    println("end  : Run")
                }
            }
            // TestCaseComponents를 TestCaseData로 변환하여 저장 호출
            val testCaseDataList = model.getAllTestCaseComponents().mapIndexed { index, component ->
                val title = (component.panel.border as? TitledBorder)?.title ?: "TestCase ${index + 1}"
                TestCaseData(
                    testCaseNumber = title.removePrefix("TestCase ").toIntOrNull() ?: index + 1,
                    input = component.inputTextArea.text.trim(),
                    output = component.outputTextArea.text.trim(),
                    answer = component.answerTextArea.text.trim(),
                    cerr = component.errorTextArea.text.trim(),
                    result = extractResultFromComponent(component) // 결과 값 추출
                )
            }
            testCaseSaver.saveTestCases(testCaseDataList)  // 변환된 리스트를 저장 호출
        }

        ui.someRunButton.addActionListener {
            if (!isRunning) {  // 실행 중이 아닐 때만 실행
                println("start: SomeRun")
                updateButtonStates(isRunning = true)
                val selectedTestCasePanels = model.getAllTestCaseComponents().filter { it.selectTestCase.isSelected }
                testCaseRunnerController.runSelectedTestCasesSequentially(selectedTestCasePanels) {
                    updateButtonStates(isRunning = false)
                    println("end  : SomeRun")
                }
            }
        }

        ui.stopButton.addActionListener {
            if (isRunning) {  // 실행 중일 때만 Stop 버튼 동작
                println("start: Stop")
                updateButtonStates(isRunning = true)
                testCaseRunnerController.requestStop()

                val runningTestCase = model.getRunningTestCase()
                runningTestCase?.let { testCase ->
                    SwingUtilities.invokeLater {
                        testCase.panel.border = BorderFactory.createTitledBorder("Stopping...")
                    }
                }

                testCaseRunnerController.onStopComplete = {
                    runningTestCase?.let { testCase ->
                        SwingUtilities.invokeLater {
                            testCase.panel.border = BorderFactory.createTitledBorder("Stopped")
                        }
                    }
                    updateButtonStates(isRunning = false)
                    println("end  : Stop")
                }
            }
        }

        ui.donateButton.addActionListener(GiveCoffeeActionListener())
        ui.guideButton.addActionListener(GuideActionListener())

        ui.fetchButton.addActionListener {
            fetchTestCaseController.onFetchTestCasesButtonClick()
        }

        ui.newTestCaseButton.addActionListener {
            val testCaseNumber = model.getAllTestCaseComponents().size + 1
            val testCasePanelUI = TestCasePanelUI(testCaseNumber)
            testCaseController = TestCaseController(model, testCasePanelUI, testCaseSaver) // testCaseSaver 전달
            val newTestCasePanel = testCaseController.createAndAddTestCasePanel(
                testCaseNumber = testCaseNumber
            ).panel
            ui.addTestCasePanel(newTestCasePanel)
        }

        ui.selectAll.addActionListener { testCaseController.selectAllTestCases(true) }
        ui.clearSelection.addActionListener { testCaseController.selectAllTestCases(false) }
    }
    // MainController.kt 파일에 추가
    private fun extractResultFromComponent(component: TestCaseComponents): String {
        val answer = component.answerTextArea.text.trim()
        val output = component.outputTextArea.text.trim()

        return when {
            answer == output -> "AC"  // 정답 일치
            answer.isEmpty() -> "CE"  // 컴파일 오류
            else -> "WA"             // 오답
        }
    }

    fun updateButtonStates(isRunning: Boolean) {
        this.isRunning = isRunning
        ui.runButton.isEnabled = !isRunning
        ui.someRunButton.isEnabled = !isRunning
        ui.stopButton.isEnabled = isRunning
        ui.newTestCaseButton.isEnabled = !isRunning
    }

    private fun updateCurrentFile(file: VirtualFile?) {
        SwingUtilities.invokeLater {
            ui.updateFileLabel(file?.name)
        }
    }
}
