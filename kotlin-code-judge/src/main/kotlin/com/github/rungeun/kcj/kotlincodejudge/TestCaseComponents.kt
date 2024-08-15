package com.github.rungeun.kcj.kotlincodejudge

import javax.swing.JCheckBox
import javax.swing.JPanel
import javax.swing.JTextArea

data class TestCaseComponents(
    val panel: JPanel,
    val selectTestCase: JCheckBox,  // 체크박스를 추가합니다.
    val inputTextArea: JTextArea,
    val outputTextArea: JTextArea,
    val answerTextArea: JTextArea,
    val errorTextArea: JTextArea
)
