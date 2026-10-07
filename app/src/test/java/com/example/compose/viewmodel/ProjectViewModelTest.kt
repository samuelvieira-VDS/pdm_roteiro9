package com.example.compose.viewmodel

import com.example.compose.model.Project
import com.example.compose.model.ProjectStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class ProjectViewModelTest {

    private lateinit var viewModel: ProjectViewModel

    @Before
    fun setUp() {
        viewModel = ProjectViewModel()
    }

    @Test
    fun testInitialProjectsLoaded() {
        val initialList = viewModel.projects.value
        assertEquals(3, initialList.size)
    }

    @Test
    fun testGetProjectById() {
        val project = viewModel.getProjectById("1")
        assertNotNull(project)
        assertEquals("Redesign do App Mobile", project?.name)

        val nonExistentProject = viewModel.getProjectById("999")
        assertNull(nonExistentProject)
    }

    @Test
    fun testSaveNewProject() {
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
    fun testUpdateExistingProject() {
        val existingProject = viewModel.getProjectById("1")!!
        val updatedProject = existingProject.copy(name = "Projeto Atualizado")

        viewModel.saveProject(updatedProject)

        val retrievedProject = viewModel.getProjectById("1")
        assertEquals("Projeto Atualizado", retrievedProject?.name)
    }

    @Test
    fun testDeleteProject() {
        viewModel.deleteProject("1")
        val updatedList = viewModel.projects.value
        assertEquals(2, updatedList.size)
        assertNull(viewModel.getProjectById("1"))
    }
}
