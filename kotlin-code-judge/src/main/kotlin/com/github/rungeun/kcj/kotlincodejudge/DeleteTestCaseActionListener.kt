package com.github.rungeun.kcj.kotlincodejudge

import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.JLabel
import javax.swing.JPanel

class DeleteTestCaseActionListener(
    private val contentPanel: JPanel,
    private val testCasePanel: JPanel,
    private val testCasePanels: MutableList<JPanel>,
    private var testCaseCount: Int
) : ActionListener {

    override fun actionPerformed(e: ActionEvent?) {
        // 부모 패널에서 해당 테스트 케이스 패널을 제거
        contentPanel.remove(testCasePanel)
        testCasePanels.remove(testCasePanel) // 리스트에서 제거
        renumberTestCases() // 테스트 케이스 재번호 매기기
        contentPanel.revalidate() // 패널 갱신
        contentPanel.repaint() // 패널 다시 그리기

        // 모든 테스트 케이스 패널이 삭제된 경우 번호 초기화
        if (testCasePanels.isEmpty()) {
            testCaseCount = 1
        }
    }

    private fun renumberTestCases() {
        testCasePanels.forEachIndexed { index, panel ->
            val topRowPanel = panel.getComponent(0) as JPanel
            val label = topRowPanel.components.firstOrNull { it is JLabel } as? JLabel
            label?.text = "UTC ${index + 1}"

            // 패널의 테두리 제목도 업데이트
            panel.border = javax.swing.BorderFactory.createTitledBorder("TestCase ${index + 1}")
        }
        testCaseCount = testCasePanels.size + 1 // 다음 테스트 케이스 번호 동기화
    }
}
