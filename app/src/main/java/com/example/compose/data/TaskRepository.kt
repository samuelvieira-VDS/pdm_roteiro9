package com.example.compose.data

import com.example.compose.model.Task
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {
    val allTasks: Flow<List<Task>> = taskDao.getAllTasks()

    suspend fun getTaskById(id: String): Task? = taskDao.getTaskById(id)

    suspend fun upsertTask(task: Task) = taskDao.upsertTask(task)

    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)
}
