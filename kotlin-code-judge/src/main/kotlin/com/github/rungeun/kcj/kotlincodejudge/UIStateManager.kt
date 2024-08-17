package com.github.rungeun.kcj.kotlincodejudge

import javax.swing.JButton
import javax.swing.JPanel

enum class UIState {
    UiFolded,
    UiMidway,
    UiExpanded
}

class UIStateManager(
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
        testCasePanel.revalidate()
        testCasePanel.repaint()
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
        currentState = state
        isExecuted = executed
        applyState(currentState)
    }

    fun getState(): UIState {
        return currentState
    }
}
