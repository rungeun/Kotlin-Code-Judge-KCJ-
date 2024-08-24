package com.github.rungeun.kcj.kotlincodejudge.controller

import java.awt.Desktop
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.net.URI

class GuideActionListener : ActionListener {
    override fun actionPerformed(e: ActionEvent) {
        val url = "https://github.com/rungeun/Kotlin-Code-Judge-KCJ-" // 열 URL 설정
        if (Desktop.isDesktopSupported()) { // Desktop API가 지원되는지 확인
            try {
                Desktop.getDesktop().browse(URI(url)) // 브라우저에서 URL 열기
            } catch (ex: Exception) {
                ex.printStackTrace() // 예외 발생 시 스택 트레이스 출력
            }
        }
    }
}


