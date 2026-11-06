package com.example.compose.data.mapper

import com.example.compose.domain.model.Priority
import com.example.compose.domain.model.Project
import com.example.compose.domain.model.ProjectStatus
import com.example.compose.domain.model.Task
import com.example.compose.domain.model.TaskStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class EntityMapperTest {
    @Test
    fun taskRoundTripPreservesEveryFieldAndEnumValue() {
        Priority.entries.forEach { priority ->
            TaskStatus.entries.forEach { status ->
                val task = Task("task-id", "Título", "Categoria", priority, status, "20/11/2026")
                assertEquals(task, task.toEntity().toDomain())
            }
        }
    }

    @Test
    fun projectRoundTripPreservesEveryFieldAndStatus() {
        ProjectStatus.entries.forEach { status ->
            val project = Project("project-id", "Projeto", "Cliente", 1234.56, status, "Descrição")
            assertEquals(project, project.toEntity().toDomain())
        }
    }
}
