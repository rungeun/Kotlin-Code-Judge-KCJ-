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
import java.io.File
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
    private fun saveTestCases() {
        val testCaseDataList = model.getAllTestCaseComponents().mapIndexed { index, component ->
            val title = (component.panel.border as? TitledBorder)?.title ?: "TestCase ${index + 1}"
            TestCaseData(
                testCaseNumber = title.removePrefix("TestCase ").toIntOrNull() ?: index + 1,
                input = component.inputTextArea.text.trim(),
                output = component.outputTextArea.text.trim(),
                answer = component.answerTextArea.text.trim(),
                cerr = component.errorTextArea.text.trim(),
                result = extractResultFromComponent(component)
            )
        }
        testCaseSaver.saveTestCases(testCaseDataList)
    }

    private fun extractResultFromComponent(component: TestCaseComponents): String {
        val answer = component.answerTextArea.text.trim()
        val output = component.outputTextArea.text.trim()
        return when {
            answer == output -> "AC"
            answer.isEmpty() -> "CE"
            else -> "WA"
        }
    }

    private fun handleFileChange() {
        val currentFile = fileTracker.getCurrentFile() ?: return
        println("Handling file change for: ${currentFile.name}") // 디버그 메시지 추가

        // buildc 폴더 확인 및 생성
        val parentDir = currentFile.parent
        val buildDir = File(parentDir.path, "buildc")
        if (!buildDir.exists()) {
            buildDir.mkdirs()
        }

        // .txt 파일 확인
        val testCaseFile = File(buildDir, "${currentFile.nameWithoutExtension}.txt")
        SwingUtilities.invokeLater {
            if (testCaseFile.exists()) {
                // 화면의 테스트 케이스를 모두 지우고 파일에서 불러오기
                println("Loading test cases from: ${testCaseFile.name}") // 디버그 메시지 추가
                testCaseController.clearTestCases()
                testCaseController.loadTestCases()
            } else {
                // 화면의 테스트 케이스만 모두 지우기
                println("No test cases found, clearing current test cases.") // 디버그 메시지 추가
                testCaseController.clearTestCases()
            }
            ui.content.revalidate() // UI 갱신
            ui.content.repaint()    // UI 다시 그리기
        }
    }


    fun updateButtonStates(isRunning: Boolean) {
        this.isRunning = isRunning
        ui.runButton.isEnabled = !isRunning
        ui.someRunButton.isEnabled = !isRunning
        ui.stopButton.isEnabled = isRunning
        ui.newTestCaseButton.isEnabled = !isRunning
    }

    // MainController.kt

    private fun updateCurrentFile(file: VirtualFile?) {
        // 파일 변경을 비동기로 처리하여 UI 멈춤 문제 해결
        SwingUtilities.invokeLater {
            ui.updateFileLabel(file?.name)

            val isKotlinFile = file?.extension == "kt"

            // 비동기로 버튼 활성화 및 테스트 케이스 로드 처리
            Thread {
                setButtonsEnabled(isKotlinFile)

                if (isKotlinFile) {
                    testCaseController.loadTestCases()  // 로드 작업 비동기 처리
                } else {
                    testCaseController.clearTestCases() // 불필요한 테스트 케이스 제거
                }
            }.start()
        }
    }

    private fun setButtonsEnabled(isEnabled: Boolean) {
        SwingUtilities.invokeLater {
            ui.runButton.isEnabled = isEnabled
            ui.someRunButton.isEnabled = isEnabled
            ui.stopButton.isEnabled = isEnabled
            ui.newTestCaseButton.isEnabled = isEnabled
            ui.selectAll.isEnabled = isEnabled
            ui.clearSelection.isEnabled = isEnabled
            ui.fetchButton.isEnabled = isEnabled
        }
    }


}
