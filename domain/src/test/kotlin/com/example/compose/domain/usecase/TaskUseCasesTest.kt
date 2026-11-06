package com.example.compose.domain.usecase

import com.example.compose.domain.model.Task
import com.example.compose.domain.repository.TaskRepository
import com.example.compose.domain.usecase.task.*
import com.example.compose.domain.validation.ValidationException
import com.example.compose.domain.validation.ValidationField
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class TaskUseCasesTest {
    private val repository = InMemoryTaskRepository()
    private val valid = Task(title = " title ", category = " category ")

    @Test
    fun rejectsBlankTitleWithoutSaving() = runBlocking {
        try {
            SaveTask(repository)(valid.copy(title = "   "))
            fail("Expected validation failure")
        } catch (error: ValidationException) {
            assertEquals(setOf(ValidationField.TASK_TITLE), error.fields)
        }
        assertNull(repository.saved)
    }
    @Test
    fun rejectsBlankCategoryWithoutSaving() = runBlocking {
        try {
            SaveTask(repository)(valid.copy(category = "   "))
            fail("Expected validation failure")
        } catch (error: ValidationException) {
            assertEquals(setOf(ValidationField.TASK_CATEGORY), error.fields)
        }
        assertNull(repository.saved)
    }

    @Test
    fun reportsAllMissingRequiredFields() = runBlocking {
        try {
            SaveTask(repository)(valid.copy(title = "", category = ""))
            fail("Expected validation failure")
        } catch (error: ValidationException) {
            assertEquals(setOf(ValidationField.TASK_TITLE, ValidationField.TASK_CATEGORY), error.fields)
        }
        assertNull(repository.saved)
    }

    @Test
    fun normalizesTextBeforePersistence() = runBlocking {
        SaveTask(repository)(valid)
        assertEquals(valid.copy(title = "title", category = "category"), repository.saved)
    }

    @Test
    fun emptyIdentifierDoesNotAccessRepository() = runBlocking {
        assertNull(GetTask(repository)(null))
        assertNull(GetTask(repository)("  "))
        DeleteTask(repository)("")
        assertEquals(0, repository.lookups)
    }

    @Test
    fun deletingMissingItemDoesNothing() = runBlocking {
        DeleteTask(repository)("missing")
        assertNull(repository.deleted)
    }

    @Test
    fun deletesExistingItemThroughContract() = runBlocking {
        SaveTask(repository)(valid)
        DeleteTask(repository)(valid.id)
        assertEquals(repository.saved, repository.deleted)
    }

    private class InMemoryTaskRepository : TaskRepository {
        override val allTasks = MutableStateFlow<List<Task>>(emptyList())
        var saved: Task? = null
        var deleted: Task? = null
        var lookups = 0

        override suspend fun getTaskById(id: String): Task? {
            lookups++
            return saved?.takeIf { it.id == id }
        }
        override suspend fun upsertTask(task: Task) { saved = task }
        override suspend fun deleteTask(task: Task) { deleted = task }
    }
}
