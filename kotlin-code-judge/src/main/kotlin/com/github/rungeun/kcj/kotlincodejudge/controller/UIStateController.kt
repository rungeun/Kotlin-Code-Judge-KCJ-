package com.github.rungeun.kcj.kotlincodejudge

import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.SwingUtilities

enum class UIState {
    UiFolded,
    UiMidway,
    UiExpanded
}

class UIStateController(
    private val testCasePanel: JPanel,
    private val inputPanel: JPanel,
    private val outputPanel: JPanel,
    private val answerPanel: JPanel,
    private val errorPanel: JPanel,
    private val uiStateButton: JButton
) {
    private var currentState: UIState = UIState.UiMidway
    private var isExecuted: Boolean = false

    init {
        applyState(currentState)
        uiStateButton.addActionListener { onUiStateButtonClicked() }
    }

    private fun applyState(state: UIState) {
        println("Applying state: $state")
        // 상태 적용 전 패널의 현재 상태를 출력하여 참조를 확인합니다.
        println("Before change - inputPanel.isVisible: ${inputPanel.isVisible}, outputPanel.isVisible: ${outputPanel.isVisible}")

        when (state) {
            UIState.UiFolded -> {
                inputPanel.isVisible = false
                outputPanel.isVisible = false
                answerPanel.isVisible = false
                errorPanel.isVisible = false
            }
            UIState.UiMidway -> {
                inputPanel.isVisible = true
                outputPanel.isVisible = true
                answerPanel.isVisible = false
                errorPanel.isVisible = false
            }
            UIState.UiExpanded -> {
                inputPanel.isVisible = true
                outputPanel.isVisible = true
                answerPanel.isVisible = true
                errorPanel.isVisible = true
            }
        }
        // 패널의 변경 사항을 적용한 후, 상태를 확인합니다.
        println("After change - inputPanel.isVisible: ${inputPanel.isVisible}, outputPanel.isVisible: ${outputPanel.isVisible}")
        refreshUI()
    }

    private fun onUiStateButtonClicked() {
        currentState = when (currentState) {
            UIState.UiFolded -> if (isExecuted) UIState.UiExpanded else UIState.UiMidway
            UIState.UiMidway -> UIState.UiFolded
            UIState.UiExpanded -> UIState.UiFolded
        }
        applyState(currentState)
    }

    fun setState(state: UIState, executed: Boolean = false) {
        println("Setting state to: $state with executed: $executed")
        if (currentState != state || this.isExecuted != executed) {
            currentState = state
            isExecuted = executed
            applyState(currentState)
        }
    }

    private fun refreshUI() {
        // 컴포넌트의 상태 변경 후 UI를 갱신합니다.
        SwingUtilities.invokeLater {
            testCasePanel.revalidate()
            testCasePanel.repaint()
            testCasePanel.topLevelAncestor?.revalidate()
            testCasePanel.topLevelAncestor?.repaint()
        }
    }
}
