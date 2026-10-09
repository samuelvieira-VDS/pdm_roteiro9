package com.example.compose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.compose.data.ProjectRepository
import com.example.compose.data.TaskRepository

class AppViewModelFactory(
    private val projectRepository: ProjectRepository?,
    private val taskRepository: TaskRepository?
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProjectViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProjectViewModel(projectRepository!!) as T
        } else if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(taskRepository!!) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
