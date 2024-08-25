package com.github.rungeun.kcj.kotlincodejudge.controller

import com.github.rungeun.kcj.kotlincodejudge.model.FetchTestCaseModel
import com.github.rungeun.kcj.kotlincodejudge.model.TestCaseModel
import com.github.rungeun.kcj.kotlincodejudge.view.MainToolWindowUI
import com.github.rungeun.kcj.kotlincodejudge.view.TestCasePanelUI
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import javax.swing.SwingUtilities


class MainController(
    private val ui: MainToolWindowUI,
    project: Project // 이 부분에서 Project 객체를 받아옴
) {
    private val model = TestCaseModel()
    private var testCaseController: TestCaseController

    private val fetchTestCaseModel = FetchTestCaseModel()
    private val fetchTestCaseController: FetchTestCaseController

    // projectBaseDir과 project를 받아서 TestCaseRunnerController에 전달
    private val testCaseRunnerController = TestCaseRunnerController(model, project.basePath ?: "", project)

    init {
        val connection = project.messageBus.connect()
        connection.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, object : FileEditorManagerListener {
            override fun selectionChanged(event: FileEditorManagerEvent) {
                updateCurrentFile(event.newFile)
            }
        })

        // 초기 파일 설정
        updateCurrentFile(FileEditorManager.getInstance(project).selectedFiles.firstOrNull())

        // 초기화
        val initialTestCasePanelUI = TestCasePanelUI(0) // 초기화용 기본 패널
        testCaseController = TestCaseController(model, initialTestCasePanelUI)
        fetchTestCaseController = FetchTestCaseController(fetchTestCaseModel, ui, testCaseController)

        // runButton 액션 리스너 추가
        ui.runButton.addActionListener {
            val testCasePanels = model.getAllTestCaseComponents()
            testCaseRunnerController.runAllTestCasesSequentially(testCasePanels)
        }

        // someRunButton 액션 리스너 추가 - 선택된 테스트 케이스만 실행
        ui.someRunButton.addActionListener {
            val selectedTestCasePanels = model.getAllTestCaseComponents().filter { it.selectTestCase.isSelected }
            testCaseRunnerController.runSelectedTestCasesSequentially(selectedTestCasePanels)
        }

        ui.donateButton.addActionListener(GiveCoffeeActionListener())
        ui.guideButton.addActionListener(GuideActionListener())

        ui.fetchButton.addActionListener {
            fetchTestCaseController.onFetchTestCasesButtonClick()
        }

        ui.newTestCaseButton.addActionListener {
            // 새로운 TestCasePanelUI와 함께 TestCaseController의 기존 인스턴스를 사용하여 테스트 케이스 추가
            val testCaseNumber = model.getAllTestCaseComponents().size + 1
            val testCasePanelUI = TestCasePanelUI(testCaseNumber)
            testCaseController = TestCaseController(model, testCasePanelUI) // 새로운 UI로 업데이트
            val newTestCasePanel = testCaseController.createAndAddTestCasePanel(
                testCaseNumber = testCaseNumber
            ).panel

            ui.addTestCasePanel(newTestCasePanel)
        }

        ui.selectAll.addActionListener { testCaseController.selectAllTestCases(true) }
        ui.clearSelection.addActionListener { testCaseController.selectAllTestCases(false) }
    }
    private fun updateCurrentFile(file: VirtualFile?) {
        SwingUtilities.invokeLater {
            ui.updateFileLabel(file?.name)
        }
    }
}
