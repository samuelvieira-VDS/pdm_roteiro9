package com.example.compose.viewmodel

import com.example.compose.domain.repository.ProjectRepository
import com.example.compose.domain.usecase.project.*
import com.example.compose.domain.model.Project
import com.example.compose.domain.model.ProjectStatus
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import com.example.compose.domain.validation.ValidationField
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class FakeProjectRepository : ProjectRepository {
    var saveFailure: Exception? = null
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

    override val allProjects: Flow<List<Project>> = projectsFlow

    override suspend fun getProjectById(id: String): Project? {
        return projectsFlow.value.find { it.id == id }
    }

    override suspend fun upsertProject(project: Project) {
        saveFailure?.let { throw it }
        val currentList = projectsFlow.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == project.id }
        if (index >= 0) {
            currentList[index] = project
        } else {
            currentList.add(project)
        }
        projectsFlow.value = currentList
    }

    override suspend fun deleteProject(project: Project) {
        projectsFlow.value = projectsFlow.value.filter { it.id != project.id }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class ProjectViewModelTest {

    private lateinit var repository: FakeProjectRepository
    private lateinit var viewModel: ProjectViewModel
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeProjectRepository()
        viewModel = ProjectViewModel(
            ObserveProjects(repository), GetProject(repository),
            SaveProject(repository), DeleteProject(repository)
        )
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

    @Test
    fun invalidInputExposesDomainErrorsAndDoesNotComplete() = runBlocking {
        var completed = false
        val original = viewModel.getProjectById("1")!!
        viewModel.saveProject(original.copy(name = "   ")) { completed = true }
        assertEquals(setOf(ValidationField.PROJECT_NAME), viewModel.validationErrors.value)
        assertEquals(original, viewModel.getProjectById("1"))
        assertFalse(completed)
        assertFalse(viewModel.isLoading.value)
        viewModel.saveProject(original) { completed = true }
        assertTrue(completed)
        assertTrue(viewModel.validationErrors.value.isEmpty())
    }

    @Test
    fun persistenceFailureResetsLoadingAndKeepsFormOpen() = runBlocking {
        var completed = false
        repository.saveFailure = IllegalStateException("storage unavailable")
        viewModel.saveProject(viewModel.getProjectById("1")!!) { completed = true }
        assertFalse(viewModel.isLoading.value)
        assertFalse(completed)
        assertNotNull(viewModel.errorMessage.value)
    }

}
