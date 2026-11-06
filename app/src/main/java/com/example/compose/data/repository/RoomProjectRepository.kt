package com.example.compose.data.repository

import com.example.compose.data.ProjectDao
import com.example.compose.data.mapper.toDomain
import com.example.compose.data.mapper.toEntity
import com.example.compose.domain.model.Project
import com.example.compose.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomProjectRepository @Inject constructor(private val dao: ProjectDao) : ProjectRepository {
    override val allProjects: Flow<List<Project>> = dao.getAllProjects().map { entities ->
        entities.map { it.toDomain() }
    }

    override suspend fun getProjectById(id: String): Project? = dao.getProjectById(id)?.toDomain()

    override suspend fun upsertProject(project: Project) { dao.upsertProject(project.toEntity()) }

    override suspend fun deleteProject(project: Project) { dao.deleteProject(project.toEntity()) }
}
