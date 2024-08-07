package com.github.rungeun.kcj.kotlincodejudge

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory


class MyToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val myToolWindowUI = MyToolWindowUI()
        val contentFactory = ContentFactory.getInstance()
        val content = contentFactory.createContent(myToolWindowUI.content, "", false)
        toolWindow.contentManager.addContent(content)
    }
}
