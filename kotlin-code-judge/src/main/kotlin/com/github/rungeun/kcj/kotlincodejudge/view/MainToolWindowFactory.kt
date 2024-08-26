package com.github.rungeun.kcj.kotlincodejudge.view

import com.github.rungeun.kcj.kotlincodejudge.controller.MainController
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

class MainToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        // View 생성
        val mainToolWindowUI = MainToolWindowUI()

        // Controller 생성 및 Project 객체 전달
        val mainController = MainController(mainToolWindowUI, project)

        // ToolWindow에 Content 추가
        val contentFactory = ContentFactory.getInstance()
        val content = contentFactory.createContent(mainToolWindowUI.content, "", false)
        toolWindow.contentManager.addContent(content)
    }
}
