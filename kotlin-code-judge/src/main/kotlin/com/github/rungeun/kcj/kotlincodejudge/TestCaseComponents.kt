package com.github.rungeun.kcj.kotlincodejudge

import javax.swing.JCheckBox
import javax.swing.JPanel
import javax.swing.JTextArea

data class TestCaseComponents(
    val panel: JPanel,
    val selectTestCase: JCheckBox,
    val inputTextArea: JTextArea,
    val outputTextArea: JTextArea,
    val answerTextArea: JTextArea,
    val errorTextArea: JTextArea,
    val uiStateManager: UIStateManager // UI 상태 관리 객체 추가
)
