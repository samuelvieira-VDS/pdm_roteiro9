package com.example.compose.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.compose.domain.model.ProjectStatus
import java.util.UUID

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val client: String,
    val budget: Double = 0.0,
    val status: ProjectStatus = ProjectStatus.PLANEJAMENTO,
    val description: String = ""
)
