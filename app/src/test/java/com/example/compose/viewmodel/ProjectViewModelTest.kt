package com.example.compose.viewmodel

import com.example.compose.data.ProjectDao
import com.example.compose.data.ProjectRepository
import com.example.compose.model.Project
import com.example.compose.model.ProjectStatus
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

class FakeProjectDao : ProjectDao {
    private val projectsFlow = MutableStateFlow<List<Project>>(
        listOf(
            Project(
                id = "1",
                name = "Redesign do App Mobile",
                client = "TechCorp Inc.",
                budget = 25000.0,
                status = ProjectStatus.EM_EXECUCAO,
                description = "Modernização completa da UI/UX com Jetpack Compose."
            ),
            Project(
                id = "2",
                name = "Sistema de Vendas Web",
                client = "Mercado Global",
                budget = 40000.0,
                status = ProjectStatus.PLANEJAMENTO,
                description = "Plataforma e-commerce B2B responsiva."
            ),
            Project(
                id = "3",
                name = "Migração para Nuvem",
                client = "Fintech Brasil",
                budget = 18000.0,
                status = ProjectStatus.CONCLUIDO,
                description = "Migração de infraestrutura para serviços de nuvem."
            )
        )
    )

    override fun getAllProjects(): Flow<List<Project>> = projectsFlow

    override suspend fun getProjectById(id: String): Project? {
        return projectsFlow.value.find { it.id == id }
    }

    override suspend fun upsertProject(project: Project): Long {
        val currentList = projectsFlow.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == project.id }
        if (index >= 0) {
            currentList[index] = project
        } else {
            currentList.add(project)
        }
        projectsFlow.value = currentList
        return 1L
    }

    override suspend fun deleteProject(project: Project): Int {
        val initialSize = projectsFlow.value.size
        projectsFlow.value = projectsFlow.value.filter { it.id != project.id }
        return initialSize - projectsFlow.value.size
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class ProjectViewModelTest {

    private lateinit var viewModel: ProjectViewModel
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        val fakeDao = FakeProjectDao()
        val repository = ProjectRepository(fakeDao)
        viewModel = ProjectViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialProjectsLoaded() {
        val initialList = viewModel.projects.value
        assertEquals(3, initialList.size)
    }

    @Test
    fun testGetProjectById() = runBlocking {
        val project = viewModel.getProjectById("1")
        assertNotNull(project)
        assertEquals("Redesign do App Mobile", project?.name)

        val nonExistentProject = viewModel.getProjectById("999")
        assertNull(nonExistentProject)
    }

    @Test
    fun testSaveNewProject() = runBlocking {
        val newProject = Project(
            id = "10",
            name = "Novo Projeto Teste",
            client = "Cliente Teste",
            budget = 50000.0,
            status = ProjectStatus.PLANEJAMENTO,
            description = "Descrição Teste"
        )
        viewModel.saveProject(newProject)

        val updatedList = viewModel.projects.value
        assertEquals(4, updatedList.size)
        val savedProject = viewModel.getProjectById("10")
        assertNotNull(savedProject)
        assertEquals("Novo Projeto Teste", savedProject?.name)
    }

    @Test
    fun testUpdateExistingProject() = runBlocking {
        val existingProject = viewModel.getProjectById("1")!!
        val updatedProject = existingProject.copy(name = "Projeto Atualizado")

        viewModel.saveProject(updatedProject)

        val retrievedProject = viewModel.getProjectById("1")
        assertEquals("Projeto Atualizado", retrievedProject?.name)
    }

    @Test
    fun testDeleteProject() = runBlocking {
        viewModel.deleteProject("1")
        val updatedList = viewModel.projects.value
        assertEquals(2, updatedList.size)
        assertNull(viewModel.getProjectById("1"))
    }
}
