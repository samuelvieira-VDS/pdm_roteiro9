package com.example.compose.domain.usecase

import com.example.compose.domain.model.Project
import com.example.compose.domain.repository.ProjectRepository
import com.example.compose.domain.usecase.project.*
import com.example.compose.domain.validation.ValidationException
import com.example.compose.domain.validation.ValidationField
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class ProjectUseCasesTest {
    private val repository = InMemoryProjectRepository()
    private val valid = Project(name = " name ", client = " client ")

    @Test
    fun rejectsBlankNameWithoutSaving() = runBlocking {
        try {
            SaveProject(repository)(valid.copy(name = "   "))
            fail("Expected validation failure")
        } catch (error: ValidationException) {
            assertEquals(setOf(ValidationField.PROJECT_NAME), error.fields)
        }
        assertNull(repository.saved)
    }
    @Test
    fun rejectsBlankClientWithoutSaving() = runBlocking {
        try {
            SaveProject(repository)(valid.copy(client = "   "))
            fail("Expected validation failure")
        } catch (error: ValidationException) {
            assertEquals(setOf(ValidationField.PROJECT_CLIENT), error.fields)
        }
        assertNull(repository.saved)
    }

    @Test
    fun reportsAllMissingRequiredFields() = runBlocking {
        try {
            SaveProject(repository)(valid.copy(name = "", client = ""))
            fail("Expected validation failure")
        } catch (error: ValidationException) {
            assertEquals(setOf(ValidationField.PROJECT_NAME, ValidationField.PROJECT_CLIENT), error.fields)
        }
        assertNull(repository.saved)
    }

    @Test
    fun normalizesTextBeforePersistence() = runBlocking {
        SaveProject(repository)(valid)
        assertEquals(valid.copy(name = "name", client = "client"), repository.saved)
    }

    @Test
    fun emptyIdentifierDoesNotAccessRepository() = runBlocking {
        assertNull(GetProject(repository)(null))
        assertNull(GetProject(repository)("  "))
        DeleteProject(repository)("")
        assertEquals(0, repository.lookups)
    }

    @Test
    fun deletingMissingItemDoesNothing() = runBlocking {
        DeleteProject(repository)("missing")
        assertNull(repository.deleted)
    }

    @Test
    fun deletesExistingItemThroughContract() = runBlocking {
        SaveProject(repository)(valid)
        DeleteProject(repository)(valid.id)
        assertEquals(repository.saved, repository.deleted)
    }

    private class InMemoryProjectRepository : ProjectRepository {
        override val allProjects = MutableStateFlow<List<Project>>(emptyList())
        var saved: Project? = null
        var deleted: Project? = null
        var lookups = 0

        override suspend fun getProjectById(id: String): Project? {
            lookups++
            return saved?.takeIf { it.id == id }
        }
        override suspend fun upsertProject(project: Project) { saved = project }
        override suspend fun deleteProject(project: Project) { deleted = project }
    }
}
