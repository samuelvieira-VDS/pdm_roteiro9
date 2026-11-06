package com.example.compose.domain.repository

import com.example.compose.domain.model.Project
import kotlinx.coroutines.flow.Flow

interface ProjectRepository {
    val allProjects: Flow<List<Project>>
    suspend fun getProjectById(id: String): Project?
    suspend fun upsertProject(project: Project)
    suspend fun deleteProject(project: Project)
}
