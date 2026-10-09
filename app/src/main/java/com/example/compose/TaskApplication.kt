package com.example.compose

import android.app.Application
import com.example.compose.data.AppDatabase
import com.example.compose.data.ProjectRepository
import com.example.compose.data.TaskRepository

class TaskApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val projectRepository by lazy { ProjectRepository(database.projectDao()) }
    val taskRepository by lazy { TaskRepository(database.taskDao()) }
}
