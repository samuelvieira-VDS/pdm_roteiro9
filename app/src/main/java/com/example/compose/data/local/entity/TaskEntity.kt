package com.example.compose.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.compose.domain.model.Priority
import com.example.compose.domain.model.TaskStatus
import java.util.UUID

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val category: String,
    val priority: Priority = Priority.MEDIA,
    val status: TaskStatus = TaskStatus.PENDENTE,
    val dueDate: String = ""
)
