package com.github.rungeun.kcj.kotlincodejudge.view

import com.github.rungeun.kcj.kotlincodejudge.controller.MainController
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

class MainToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val projectBaseDir = project.basePath ?: ""
        val mainToolWindowUI = MainToolWindowUI()  // View 생성
        val mainController = MainController(mainToolWindowUI)  // Controller 생성
        val contentFactory = ContentFactory.getInstance()
        val content = contentFactory.createContent(mainToolWindowUI.content, "", false)
        toolWindow.contentManager.addContent(content)
    }
}
