package com.github.rungeun.kcj.kotlincodejudge

import javax.swing.JPanel
import javax.swing.JTextArea
import javax.swing.JCheckBox

data class TestCaseComponents(
    val panel: JPanel,
    val selectTestCase: JCheckBox,
    val inputTextArea: JTextArea,
    val outputTextArea: JTextArea,
    val answerTextArea: JTextArea,
    val errorTextArea: JTextArea,
    val uiStateManager: UIStateManager
)
