package com.example.compose.data.repository

import com.example.compose.data.TaskDao
import com.example.compose.data.mapper.toDomain
import com.example.compose.data.mapper.toEntity
import com.example.compose.domain.model.Task
import com.example.compose.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomTaskRepository @Inject constructor(private val dao: TaskDao) : TaskRepository {
    override val allTasks: Flow<List<Task>> = dao.getAllTasks().map { entities ->
        entities.map { it.toDomain() }
    }

    override suspend fun getTaskById(id: String): Task? = dao.getTaskById(id)?.toDomain()

    override suspend fun upsertTask(task: Task) { dao.upsertTask(task.toEntity()) }

    override suspend fun deleteTask(task: Task) { dao.deleteTask(task.toEntity()) }
}
