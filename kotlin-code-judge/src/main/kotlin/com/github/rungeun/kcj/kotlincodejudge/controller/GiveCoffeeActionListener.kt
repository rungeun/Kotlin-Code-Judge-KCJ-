package com.github.rungeun.kcj.kotlincodejudge.controller

import java.awt.Desktop
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.net.URI

class GiveCoffeeActionListener : ActionListener {

    override fun actionPerformed(e: ActionEvent) {
        val url = "https://www.paypal.com/paypalme/rungeun/4.95"
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(URI(url))
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }
}
