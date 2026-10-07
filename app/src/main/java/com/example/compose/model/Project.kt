package com.example.compose.model

import java.util.UUID

enum class ProjectStatus(val label: String) {
    PLANEJAMENTO("Em Planejamento"),
    EM_EXECUCAO("Em Execução"),
    CONCLUIDO("Concluído"),
    PAUSADO("Pausado")
}

data class Project(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val client: String,
    val budget: Double = 0.0,
    val status: ProjectStatus = ProjectStatus.PLANEJAMENTO,
    val description: String = ""
)
