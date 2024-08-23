package com.github.rungeun.kcj.kotlincodejudge

import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.messages.MessageBusConnection
import java.awt.*
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.*
import javax.swing.border.TitledBorder
import java.io.*

class MyToolWindowUI(val projectBaseDir: String, val project: Project) {
    val content: JPanel = JPanel()

    private val outerBackgroundColor: JBColor = JBColor.GREEN
    private val innerBackgroundColor: JBColor = JBColor.WHITE

    private lateinit var testCaseManager: TestCaseManager
    private lateinit var saveManager: SaveManager
    private lateinit var testCaseRunner: TestCaseRunner
    private lateinit var runButton: JButton
    private lateinit var someRunButton: JButton
    private lateinit var stopButton: JButton
    private lateinit var newTestCaseButton: JButton
    private lateinit var nowLabel: JBLabel // Now: (현재 파일명) 표시 라벨

    init {
        val testCasePanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            background = innerBackgroundColor
            isOpaque = true
            border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
            minimumSize = Dimension(200, 300)
        }

        testCaseManager = TestCaseManager(testCasePanel)
        saveManager = SaveManager(project, testCaseManager)

        loadTestCasesFromSave()  // 파일에서 데이터 로드

        saveManager.setupFileChangeListener(::onFileChanged)  // 파일 변경 시 동작할 함수 지정
        setupAutoSaveListeners()

        // UI 요소들 초기화
        initializeUIComponents(testCasePanel)
        updateNowLabel()  // 초기 파일명 설정
    }

    private fun updateNowLabel() {
        val currentFile = FileEditorManager.getInstance(project).selectedEditor?.file
        val currentFileName = currentFile?.name ?: "No file"
        nowLabel.text = "Now: $currentFileName"
    }

    private fun onFileChanged(newFile: VirtualFile) {
        // 파일 변경이 감지되면 현재 테스트 케이스들을 저장
        saveTestCases()

        // 기존 테스트 케이스들 제거
        testCaseManager.clearAllTestCases()

        // 새 파일의 테스트 케이스들을 로드
        loadTestCasesFromSave(newFile)

        // 파일명이 변경된 경우 라벨 업데이트
        updateNowLabelWithVirtualFile(newFile)
    }

    private fun updateNowLabelWithVirtualFile(newFile: VirtualFile) {
        nowLabel.text = "Now: ${newFile.name}"
    }

    private fun initializeUIComponents(testCasePanel: JPanel) {
        content.layout = BoxLayout(content, BoxLayout.Y_AXIS)
        content.background = outerBackgroundColor

        val topPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            background = innerBackgroundColor
            isOpaque = true
            border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
        }

        nowLabel = JBLabel("Now: ").apply {
            foreground = JBColor.BLACK
        }

        topPanel.add(nowLabel)
        topPanel.add(Box.createHorizontalGlue())

        content.add(topPanel)

        // 나머지 UI 초기화 코드

        val fetchPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            background = innerBackgroundColor
            isOpaque = true
            border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
        }

        val fetchLabel = JBLabel("ProblemNumber: ")
        val fetchTextField = JTextField(7).apply {
            preferredSize = Dimension(60, 30)
            maximumSize = Dimension(70, 30)
        }
        val fetchButton = JButton("Fetch Test Cases")

        fetchPanel.add(fetchLabel)
        fetchPanel.add(fetchTextField)
        fetchPanel.add(fetchButton)

        val buttonPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            background = innerBackgroundColor
            isOpaque = true
            border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
        }

        val row1Panel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            isOpaque = false
        }
        runButton = JButton("Run")
        someRunButton = JButton("Some Run")
        stopButton = JButton("Stop")

        row1Panel.add(runButton)
        row1Panel.add(someRunButton)
        row1Panel.add(stopButton)

        val row2Panel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            isOpaque = false
        }
        val donateButton = JButton("Give coffee :>")
        val guideButton = JButton("Guide")
        donateButton.addActionListener(GiveCoffeeActionListener())
        guideButton.addActionListener(GuideActionListener())

        row2Panel.add(donateButton)
        row2Panel.add(guideButton)

        val row3Panel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            isOpaque = false
        }
        val selectAll = JButton("All")
        val clearSelection = JButton("Clear")

        row3Panel.add(selectAll)
        row3Panel.add(clearSelection)

        buttonPanel.add(row1Panel)
        buttonPanel.add(row2Panel)
        buttonPanel.add(row3Panel)

        val scrollPane = JBScrollPane(testCasePanel).apply {
            verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
            horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
            verticalScrollBar.unitIncrement = 16
        }

        val addButtonPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            background = innerBackgroundColor
            isOpaque = true
            border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
        }

        newTestCaseButton = JButton("New TestCase")
        val newTestCasePanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            isOpaque = false
        }
        newTestCasePanel.add(newTestCaseButton)

        // 'All' 버튼 클릭 시 모든 체크박스 선택
        selectAll.addActionListener {
            testCaseManager.selectAllTestCases(true)
        }

        // 'Clear' 버튼 클릭 시 모든 체크박스 해제
        clearSelection.addActionListener {
            testCaseManager.selectAllTestCases(false)
        }

        newTestCaseButton.addActionListener {
            testCaseManager.addNewTestCase()
        }

        addButtonPanel.add(newTestCasePanel)

        fetchButton.addActionListener(FetchTestCaseActionListener(fetchTextField, testCaseManager, fetchLabel))

        testCaseRunner = TestCaseRunner(
            projectBaseDir, project,
            onExecutionFinished = {
                runButton.isEnabled = true
                someRunButton.isEnabled = true
                newTestCaseButton.isEnabled = true
                stopButton.isEnabled = false
            },
            onTestCaseFinished = { utcNumber, result ->
                onTestCaseFinished(result, utcNumber)
            }
        )

        runButton.addActionListener {
            disableButtonsDuringExecution()
            testCaseRunner.runAllTestCasesSequentially(testCaseManager.getAllTestCaseComponents())
            saveTestCases()  // 실행 후 테스트 케이스 저장
        }

        someRunButton.addActionListener {
            disableButtonsDuringExecution()
            testCaseRunner.runSelectedTestCasesSequentially(testCaseManager.getSelectedTestCaseComponents())
            saveTestCases()  // 실행 후 테스트 케이스 저장
        }

        stopButton.addActionListener {
            stopButton.isEnabled = false
            testCaseRunner.requestStop()
            testCaseManager.getRunningTestCase()?.let {
                SwingUtilities.invokeLater {
                    val border = it.panel.border
                    if (border is TitledBorder) {
                        it.panel.border = BorderFactory.createTitledBorder("Stopping...")
                    }
                }
            }
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

        stopButton.isEnabled = false
    }

    private fun disableButtonsDuringExecution() {
        runButton.isEnabled = false
        someRunButton.isEnabled = false
        newTestCaseButton.isEnabled = false
        stopButton.isEnabled = true
    }

    private fun onTestCaseFinished(result: String, utcNumber: Int) {
        when (result) {
            "AC" -> testCaseManager.setUiStateForTestCase(utcNumber, UIState.UiFolded)
            else -> testCaseManager.setUiStateForTestCase(utcNumber, UIState.UiExpanded)
        }
    }


    private fun loadTestCasesFromSave(newFile: VirtualFile) {
        val savedTestCases = saveManager.loadValues(newFile)
        if (savedTestCases.isNotEmpty()) {
            println("Loaded ${savedTestCases.size} test cases.")  // 디버깅 메시지
            testCaseManager.clearAllTestCases()
            testCaseManager.addTestCases(savedTestCases)
        } else {
            println("No test cases found in the file.")
        }
    }


    private fun loadTestCasesFromSave() {
        val currentFilePath = saveManager.getCurrentFilePath()  // 경로를 가져옴
        if (currentFilePath != null) {
            val virtualFile = VirtualFileManager.getInstance().findFileByUrl("file://$currentFilePath")
            if (virtualFile != null) {
                val savedTestCases = saveManager.loadValues(virtualFile)
                if (savedTestCases.isNotEmpty()) {
                    println("Loaded ${savedTestCases.size} test cases.")  // 디버깅 메시지
                    testCaseManager.clearAllTestCases()
                    testCaseManager.addTestCases(savedTestCases)
                } else {
                    println("No test cases found in the file.")
                }
            } else {
                println("VirtualFile not found for the current file path.")
            }
        } else {
            println("No current file found to load test cases from.")
        }
    }




    private fun setupAutoSaveListeners() {
        // 데이터 변경 시 자동 저장 설정
        testCaseManager.getAllTestCaseComponents().forEach { testCase ->
            val autoSaveListener = object : KeyAdapter() {
                override fun keyReleased(e: KeyEvent) {
                    saveTestCases()
                }
            }
            testCase.inputTextArea.addKeyListener(autoSaveListener)
            testCase.outputTextArea.addKeyListener(autoSaveListener)
            testCase.answerTextArea.addKeyListener(autoSaveListener)
            testCase.errorTextArea.addKeyListener(autoSaveListener)
        }
    }


    private fun saveTestCases() {
        saveManager.saveTestCases(testCaseManager.getAllTestCaseComponents().map {
            TestCase(
                input = it.inputTextArea.text,
                output = it.outputTextArea.text,
                answer = it.answerTextArea.text,
                cerr = it.errorTextArea.text,
                result = it.uiStateManager.getResult()  // 실제 결과 가져오기
            )
        })
    }   

}
