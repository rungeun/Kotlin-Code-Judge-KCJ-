package com.github.rungeun.kcj.kotlincodejudge

import java.awt.Desktop
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.net.URI

class GiveCoffeeActionListener : ActionListener {

    override fun actionPerformed(e: ActionEvent) {
        val url = "https://github.com/rungeun/Kotlin-Code-Judge-KCJ-"
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(URI(url))
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }
}
