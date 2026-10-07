package com.example.compose.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.Task
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String? = null,
    val icon: ImageVector? = null
) {
    object TaskList : Screen("tasks_list", "Tarefas", Icons.Default.Task)
    object ProjectList : Screen("projects_list", "Projetos", Icons.Default.FolderSpecial)
    
    object TaskForm : Screen("task_form?taskId={taskId}") {
        fun createRoute(taskId: String? = null): String {
            return if (!taskId.isNullOrBlank()) "task_form?taskId=$taskId" else "task_form"
        }
    }
    
    object ProjectForm : Screen("project_form?projectId={projectId}") {
        fun createRoute(projectId: String? = null): String {
            return if (!projectId.isNullOrBlank()) "project_form?projectId=$projectId" else "project_form"
        }
    }
}

val bottomNavItems = listOf(
    Screen.TaskList,
    Screen.ProjectList
)
