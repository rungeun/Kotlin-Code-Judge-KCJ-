package com.github.rungeun.kcj.kotlincodejudge

import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.JPanel

class AddTestCaseActionListener(private val testCaseManager: TestCaseManager) : ActionListener {

    override fun actionPerformed(e: ActionEvent?) {
        testCaseManager.addNewTestCase()
    }
}
