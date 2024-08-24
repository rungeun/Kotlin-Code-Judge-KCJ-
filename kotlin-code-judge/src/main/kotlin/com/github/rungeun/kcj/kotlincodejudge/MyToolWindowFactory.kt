package com.github.rungeun.kcj.kotlincodejudge

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

class MyToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val projectBaseDir = project.basePath ?: ""
        val myToolWindowUI = MyToolWindowUI(projectBaseDir, project)  // project 객체를 전달합니다.
        val contentFactory = ContentFactory.getInstance()
        val content = contentFactory.createContent(myToolWindowUI.content, "", false)
        toolWindow.contentManager.addContent(content)
    }
}
