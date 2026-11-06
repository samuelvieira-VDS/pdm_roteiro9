package com.example.compose.domain.repository

import com.example.compose.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    val allTasks: Flow<List<Task>>
    suspend fun getTaskById(id: String): Task?
    suspend fun upsertTask(task: Task)
    suspend fun deleteTask(task: Task)
}
