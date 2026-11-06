package com.example.compose.data.mapper

import com.example.compose.data.local.entity.ProjectEntity
import com.example.compose.domain.model.Project

fun ProjectEntity.toDomain(): Project = Project(id = id, name = name, client = client, budget = budget, status = status, description = description)

fun Project.toEntity(): ProjectEntity = ProjectEntity(id = id, name = name, client = client, budget = budget, status = status, description = description)
