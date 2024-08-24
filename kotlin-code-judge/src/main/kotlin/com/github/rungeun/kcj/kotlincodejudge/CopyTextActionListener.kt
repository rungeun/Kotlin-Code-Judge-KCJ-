package com.github.rungeun.kcj.kotlincodejudge

import javax.swing.JButton
import javax.swing.Timer
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

class CopyTextActionListener(private val button: JButton, private val textProvider: () -> String) : java.awt.event.ActionListener {
    private var timer: Timer? = null

    override fun actionPerformed(e: java.awt.event.ActionEvent?) {
        val text = textProvider()
        val stringSelection = StringSelection(text)
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        clipboard.setContents(stringSelection, null)

        // 버튼 텍스트를 "Copied!"로 변경 후 타이머를 사용해 원래 텍스트로 돌려놓음
        val originalText = "Copy"  //button.text
        button.text = "Copied!"

        // 기존 타이머가 있으면 중지
        timer?.stop()

        // 새로운 타이머 설정
        timer = Timer(800) {
            button.text = originalText
        }
        timer?.start()
    }
}
