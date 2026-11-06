package com.example.compose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compose.domain.model.Project
import com.example.compose.domain.usecase.project.DeleteProject
import com.example.compose.domain.usecase.project.GetProject
import com.example.compose.domain.usecase.project.ObserveProjects
import com.example.compose.domain.usecase.project.SaveProject
import com.example.compose.domain.validation.ValidationException
import com.example.compose.domain.validation.ValidationField
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjectViewModel @Inject constructor(
    observeProjects: ObserveProjects,
    private val getProject: GetProject,
    private val saveProject: SaveProject,
    private val deleteProject: DeleteProject
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    private val _validationErrors = MutableStateFlow<Set<ValidationField>>(emptySet())
    val validationErrors = _validationErrors.asStateFlow()
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    val projects: StateFlow<List<Project>> = observeProjects()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    suspend fun getProjectById(id: String?): Project? = getProject(id)

    fun clearErrors() {
        _validationErrors.value = emptySet()
        _errorMessage.value = null
    }

    fun saveProject(project: Project, onComplete: () -> Unit = {}) = performAction {
        saveProject.invoke(project)
        onComplete()
    }

    fun deleteProject(id: String) = performAction { deleteProject.invoke(id) }

    private fun performAction(action: suspend () -> Unit) {
        viewModelScope.launch {
            clearErrors()
            _isLoading.value = true
            try {
                action()
            } catch (error: CancellationException) {
                throw error
            } catch (error: ValidationException) {
                _validationErrors.value = error.fields
            } catch (error: Exception) {
                _errorMessage.value = "Não foi possível concluir a operação. Tente novamente."
            } finally {
                _isLoading.value = false
            }
        }
    }
}
