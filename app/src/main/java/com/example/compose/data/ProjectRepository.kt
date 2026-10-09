package com.example.compose.data

import com.example.compose.model.Project
import kotlinx.coroutines.flow.Flow

class ProjectRepository(private val projectDao: ProjectDao) {
    val allProjects: Flow<List<Project>> = projectDao.getAllProjects()

    suspend fun getProjectById(id: String): Project? = projectDao.getProjectById(id)

    suspend fun upsertProject(project: Project) = projectDao.upsertProject(project)

    suspend fun deleteProject(project: Project) = projectDao.deleteProject(project)
}
