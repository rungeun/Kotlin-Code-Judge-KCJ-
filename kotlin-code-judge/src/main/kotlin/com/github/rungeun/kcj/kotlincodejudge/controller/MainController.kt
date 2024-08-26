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
import javax.swing.BorderFactory
import javax.swing.SwingUtilities

class MainController(private val ui: MainToolWindowUI, project: Project) {
    private val model = TestCaseModel()
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

        val initialTestCasePanelUI = TestCasePanelUI(0)
        testCaseController = TestCaseController(model, initialTestCasePanelUI)
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
            testCaseController = TestCaseController(model, testCasePanelUI)
            val newTestCasePanel = testCaseController.createAndAddTestCasePanel(
                testCaseNumber = testCaseNumber
            ).panel
            ui.addTestCasePanel(newTestCasePanel)
        }

        ui.selectAll.addActionListener { testCaseController.selectAllTestCases(true) }
        ui.clearSelection.addActionListener { testCaseController.selectAllTestCases(false) }
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
