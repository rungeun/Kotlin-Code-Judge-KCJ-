package com.github.rungeun.kcj.kotlincodejudge

import javax.swing.JPanel
import javax.swing.JTextArea

data class TestCaseComponents(
    val panel: JPanel,
    val inputTextArea: JTextArea,
    val outputTextArea: JTextArea,
    val answerTextArea: JTextArea,
    val errorTextArea: JTextArea
)
