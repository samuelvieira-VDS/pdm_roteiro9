package com.example.compose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compose.data.ProjectRepository
import com.example.compose.model.Project
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProjectViewModel(private val repository: ProjectRepository) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val projects: StateFlow<List<Project>> = repository.allProjects
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    suspend fun getProjectById(id: String?): Project? {
        if (id.isNullOrBlank()) return null
        return repository.getProjectById(id)
    }

    fun saveProject(project: Project, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.upsertProject(project)
            _isLoading.value = false
            onComplete()
        }
    }

    fun deleteProject(id: String) {
        viewModelScope.launch {
            val project = repository.getProjectById(id)
            project?.let {
                _isLoading.value = true
                repository.deleteProject(it)
                _isLoading.value = false
            }
        }
    }
}
