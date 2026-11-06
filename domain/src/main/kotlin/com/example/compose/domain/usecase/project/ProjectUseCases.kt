package com.example.compose.domain.usecase.project

import com.example.compose.domain.model.Project
import com.example.compose.domain.repository.ProjectRepository
import com.example.compose.domain.validation.ValidationException
import com.example.compose.domain.validation.ValidationField
import kotlinx.coroutines.flow.Flow

class ObserveProjects(private val repository: ProjectRepository) {
    operator fun invoke(): Flow<List<Project>> = repository.allProjects
}

class GetProject(private val repository: ProjectRepository) {
    suspend operator fun invoke(id: String?): Project? =
        if (id.isNullOrBlank()) null else repository.getProjectById(id)
}

class SaveProject(private val repository: ProjectRepository) {
    suspend operator fun invoke(project: Project) {
        val errors = buildSet {
            if (project.name.isBlank()) add(ValidationField.PROJECT_NAME)
            if (project.client.isBlank()) add(ValidationField.PROJECT_CLIENT)
        }
        if (errors.isNotEmpty()) throw ValidationException(errors)
        repository.upsertProject(project.copy(name = project.name.trim(), client = project.client.trim(), description = project.description.trim()))
    }
}

class DeleteProject(private val repository: ProjectRepository) {
    suspend operator fun invoke(id: String) {
        if (id.isBlank()) return
        repository.getProjectById(id)?.let { repository.deleteProject(it) }
    }
}
