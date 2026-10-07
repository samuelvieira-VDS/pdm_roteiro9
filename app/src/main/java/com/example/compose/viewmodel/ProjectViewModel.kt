package com.example.compose.viewmodel

import androidx.lifecycle.ViewModel
import com.example.compose.model.Project
import com.example.compose.model.ProjectStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProjectViewModel : ViewModel() {
    private val _projects = MutableStateFlow<List<Project>>(
        listOf(
            Project(
                id = "1",
                name = "Redesign do App Mobile",
                client = "TechCorp Inc.",
                budget = 25000.0,
                status = ProjectStatus.EM_EXECUCAO,
                description = "Modernização completa da UI/UX com Jetpack Compose."
            ),
            Project(
                id = "2",
                name = "Sistema de Vendas Web",
                client = "Mercado Global",
                budget = 40000.0,
                status = ProjectStatus.PLANEJAMENTO,
                description = "Plataforma e-commerce B2B responsiva."
            ),
            Project(
                id = "3",
                name = "Migração para Nuvem",
                client = "Fintech Brasil",
                budget = 18000.0,
                status = ProjectStatus.CONCLUIDO,
                description = "Migração de infraestrutura para serviços de nuvem."
            )
        )
    )
    val projects: StateFlow<List<Project>> = _projects.asStateFlow()

    fun getProjectById(id: String?): Project? {
        if (id.isNull_or_blank()) return null
        return _projects.value.find { it.id == id }
    }

    fun saveProject(project: Project) {
        _projects.update { currentList ->
            val index = currentList.indexOfFirst { it.id == project.id }
            if (index >= 0) {
                currentList.toMutableList().apply { set(index, project) }
            } else {
                currentList + project
            }
        }
    }

    fun deleteProject(id: String) {
        _projects.update { currentList -> currentList.filter { it.id != id } }
    }
}

private fun String?.isNull_or_blank(): Boolean = this == null || this.isBlank()
