package com.example.compose.data.mapper

import com.example.compose.data.local.entity.TaskEntity
import com.example.compose.domain.model.Task

fun TaskEntity.toDomain(): Task = Task(id = id, title = title, category = category, priority = priority, status = status, dueDate = dueDate)

fun Task.toEntity(): TaskEntity = TaskEntity(id = id, title = title, category = category, priority = priority, status = status, dueDate = dueDate)
