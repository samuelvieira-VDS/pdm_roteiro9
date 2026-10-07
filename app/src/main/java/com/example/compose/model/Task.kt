package com.example.compose.model

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

data class Task(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val category: String,
    val priority: Priority = Priority.MEDIA,
    val status: TaskStatus = TaskStatus.PENDENTE,
    val dueDate: String = ""
)
