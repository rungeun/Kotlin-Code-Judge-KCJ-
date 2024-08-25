package com.github.rungeun.kcj.kotlincodejudge.model

import com.github.rungeun.kcj.kotlincodejudge.TestCaseComponents
import com.github.rungeun.kcj.kotlincodejudge.UIState
import com.github.rungeun.kcj.kotlincodejudge.UIStateManager
import javax.swing.BorderFactory
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.border.TitledBorder

class TestCaseModel {

    private var testCaseCount = 1
    private val testCasePanels = mutableListOf<TestCaseComponents>()
    private var runningTestCase: TestCaseComponents? = null
    private val uiStateManagers = mutableMapOf<Int, UIStateManager>()

    fun addTestCaseComponent(testCaseComponent: TestCaseComponents) {
        testCasePanels.add(testCaseComponent)
        testCaseCount++
    }

    fun getAllTestCaseComponents(): List<TestCaseComponents> {
        return testCasePanels
    }

    fun getSelectedTestCaseComponents(): List<TestCaseComponents> {
        return testCasePanels.filter { it.selectTestCase.isSelected }
    }

    fun setRunningTestCase(testCaseComponent: TestCaseComponents) {
        runningTestCase = testCaseComponent
    }

    fun getRunningTestCase(): TestCaseComponents? {
        return testCasePanels.find {
            val border = it.panel.border
            border is TitledBorder && border.title.startsWith("Judging")
        }
    }


    fun setUiStateForTestCase(utcNumber: Int, state: UIState) {
        if (utcNumber < 0 || utcNumber >= testCasePanels.size) {
            println("Error: Invalid test case number: $utcNumber")
            return
        }
        uiStateManagers[utcNumber]?.setState(state)
    }

    //////////////
    fun removeTestCaseComponent(testCaseComponent: TestCaseComponents) {
        testCasePanels.remove(testCaseComponent)
        renumberTestCases() // 테스트 케이스가 제거될 때 번호를 다시 매깁니다.
    }
    private fun renumberTestCases() {
        testCasePanels.forEachIndexed { index, testCase ->
            val topRowPanel = testCase.panel.getComponent(0) as JPanel
            val testCaseLabel = findLabelInPanel(topRowPanel)
            if (testCaseLabel != null) {
                testCaseLabel.text = "UTC ${index + 1}"
            } else {
                println("Warning: No JLabel found in Panel for TestCase ${index + 1}")
            }
            testCase.panel.border = BorderFactory.createTitledBorder("TestCase ${index + 1}")
        }
    }

    private fun findLabelInPanel(panel: JPanel): JLabel? {
        for (component in panel.components) {
            if (component is JLabel) {
                return component
            } else if (component is JPanel) {
                val found = findLabelInPanel(component)
                if (found != null) return found
            }
        }
        return null
    }
}
