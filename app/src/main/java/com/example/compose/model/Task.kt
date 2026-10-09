package com.example.compose.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

enum class Priority(val label: String) {
    BAIXA("Baixa"),
    MEDIA("Média"),
    ALTA("Alta")
}

enum class TaskStatus(val label: String) {
    PENDENTE("Pendente"),
    EM_ANDAMENTO("Em Andamento"),
    CONCLUIDA("Concluída")
}

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val category: String,
    val priority: Priority = Priority.MEDIA,
    val status: TaskStatus = TaskStatus.PENDENTE,
    val dueDate: String = ""
)
