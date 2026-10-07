package com.example.compose.viewmodel

import com.example.compose.model.Priority
import com.example.compose.model.Task
import com.example.compose.model.TaskStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class TaskViewModelTest {

    private lateinit var viewModel: TaskViewModel

    @Before
    fun setUp() {
        viewModel = TaskViewModel()
    }

    @Test
    fun testInitialTasksLoaded() {
        val initialList = viewModel.tasks.value
        assertEquals(3, initialList.size)
    }

    @Test
    fun testGetTaskById() {
        val task = viewModel.getTaskById("1")
        assertNotNull(task)
        assertEquals("Criar protótipo da interface", task?.title)

        val nonExistentTask = viewModel.getTaskById("999")
        assertNull(nonExistentTask)
    }

    @Test
    fun testSaveNewTask() {
        val newTask = Task(
            id = "10",
            title = "Nova Tarefa de Teste",
            category = "Teste",
            priority = Priority.ALTA,
            status = TaskStatus.PENDENTE,
            dueDate = "01/12/2026"
        )
        viewModel.saveTask(newTask)

        val updatedList = viewModel.tasks.value
        assertEquals(4, updatedList.size)
        val savedTask = viewModel.getTaskById("10")
        assertNotNull(savedTask)
        assertEquals("Nova Tarefa de Teste", savedTask?.title)
    }

    @Test
    fun testUpdateExistingTask() {
        val existingTask = viewModel.getTaskById("1")!!
        val updatedTask = existingTask.copy(title = "Título Atualizado")

        viewModel.saveTask(updatedTask)

        val retrievedTask = viewModel.getTaskById("1")
        assertEquals("Título Atualizado", retrievedTask?.title)
    }

    @Test
    fun testDeleteTask() {
        viewModel.deleteTask("1")
        val updatedList = viewModel.tasks.value
        assertEquals(2, updatedList.size)
        assertNull(viewModel.getTaskById("1"))
    }
}
