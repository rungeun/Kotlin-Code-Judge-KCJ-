fun main() = with(System.`in`.bufferedReader()) {
    val A = readLine().toInt()
    for (i in 0..A) {
        for (j in 0..i) {
            print("$j ")
        }//
        println()
        System.err.println(i)
    }
}
/*
StopActionListener.kt

JOptionPane.showMessageDialog(null, "inputTextArea: $inputTextArea \n outputTextArea: $outputTextArea \n  answerTextArea: $answerTextArea \n errorTextArea: $errorTextArea", "Debug", JOptionPane.INFORMATION_MESSAGE)
JOptionPane.showMessageDialog(null, "Debug: $errorTextArea.text", "Debug", JOptionPane.INFORMATION_MESSAGE)


    SwingUtilities.invokeLater {
                    testCase.panel.border = BorderFactory.createTitledBorder("Judging...")
                }


selectTestCase
        testCaseRunner = TestCaseRunner(projectBaseDir, project) { utcNumber, result ->
            onTestCaseFinished(result, utcNumber)
        }
0
       TestCaseComponents(
                newTestCasePanel,
                selectTestCase,
          0     inputTextArea,
                outputTextArea,
                answerTextArea,
                errorTextArea
            )



        runButton.addActionListener {
            testCaseRunner.runAllTestCasesSequentially(testCaseManager.getAllTestCaseComponents())
        }

        someRunButton.addActionListener {
            val selectedTestCases = 

        }
 */