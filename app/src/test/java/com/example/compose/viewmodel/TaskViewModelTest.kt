package com.example.compose.viewmodel

import com.example.compose.data.TaskDao
import com.example.compose.data.TaskRepository
import com.example.compose.model.Priority
import com.example.compose.model.Task
import com.example.compose.model.TaskStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class FakeTaskDao : TaskDao {
    private val tasksFlow = MutableStateFlow<List<Task>>(
        listOf(
            Task(
                id = "1",
                title = "Criar protótipo da interface",
                category = "Design",
                priority = Priority.ALTA,
                status = TaskStatus.EM_ANDAMENTO,
                dueDate = "15/11/2026"
            ),
            Task(
                id = "2",
                title = "Implementar autenticação",
                category = "Backend",
                priority = Priority.ALTA,
                status = TaskStatus.PENDENTE,
                dueDate = "20/11/2026"
            ),
            Task(
                id = "3",
                title = "Revisar documentação da API",
                category = "Documentação",
                priority = Priority.BAIXA,
                status = TaskStatus.CONCLUIDA,
                dueDate = "10/11/2026"
            )
        )
    )

    override fun getAllTasks(): Flow<List<Task>> = tasksFlow

    override suspend fun getTaskById(id: String): Task? {
        return tasksFlow.value.find { it.id == id }
    }

    override suspend fun upsertTask(task: Task): Long {
        val currentList = tasksFlow.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == task.id }
        if (index >= 0) {
            currentList[index] = task
        } else {
            currentList.add(task)
        }
        tasksFlow.value = currentList
        return 1L
    }

    override suspend fun deleteTask(task: Task): Int {
        val initialSize = tasksFlow.value.size
        tasksFlow.value = tasksFlow.value.filter { it.id != task.id }
        return initialSize - tasksFlow.value.size
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class TaskViewModelTest {

    private lateinit var viewModel: TaskViewModel
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        val fakeDao = FakeTaskDao()
        val repository = TaskRepository(fakeDao)
        viewModel = TaskViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialTasksLoaded() {
        val initialList = viewModel.tasks.value
        assertEquals(3, initialList.size)
    }

    @Test
    fun testGetTaskById() = runBlocking {
        val task = viewModel.getTaskById("1")
        assertNotNull(task)
        assertEquals("Criar protótipo da interface", task?.title)

        val nonExistentTask = viewModel.getTaskById("999")
        assertNull(nonExistentTask)
    }

    @Test
    fun testSaveNewTask() = runBlocking {
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
    fun testUpdateExistingTask() = runBlocking {
        val existingTask = viewModel.getTaskById("1")!!
        val updatedTask = existingTask.copy(title = "Título Atualizado")

        viewModel.saveTask(updatedTask)

        val retrievedTask = viewModel.getTaskById("1")
        assertEquals("Título Atualizado", retrievedTask?.title)
    }

    @Test
    fun testDeleteTask() = runBlocking {
        viewModel.deleteTask("1")
        val updatedList = viewModel.tasks.value
        assertEquals(2, updatedList.size)
        assertNull(viewModel.getTaskById("1"))
    }
}
