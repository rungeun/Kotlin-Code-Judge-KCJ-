package com.github.rungeun.kcj.kotlincodejudge

import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.JOptionPane
import javax.swing.SwingUtilities

class StopButtonActionListener(
    private val testCaseRunner: TestCaseRunner,
    private val testCaseManager: TestCaseManager,
    private val runButton: JButton,
    private val someRunButton: JButton,
    private val newTestCaseButton: JButton,
) : ActionListener {

    override fun actionPerformed(e: ActionEvent?) {
        // Run과 Some Run 버튼을 비활성화
        SwingUtilities.invokeLater {
            runButton.isEnabled = false
            someRunButton.isEnabled = false
            newTestCaseButton.isEnabled = false
        }

        // 현재 실행 중인 테스트 케이스의 제목을 "Stopping..."으로 변경
        testCaseManager.getRunningTestCase()?.let {
            SwingUtilities.invokeLater {
                it.panel.border = BorderFactory.createTitledBorder("Stopping...")
            }
        }

        // 정지 요청
        testCaseManager.getRunningTestCase()?.let {
            JOptionPane.showMessageDialog(null, "Debug: Updating UI to Stopped", "Debug", JOptionPane.INFORMATION_MESSAGE)
            SwingUtilities.invokeLater {
                it.panel.border = BorderFactory.createTitledBorder("Stopped")
                runButton.isEnabled = true
                someRunButton.isEnabled = true
                newTestCaseButton.isEnabled = true
            }
        }
    }
}
