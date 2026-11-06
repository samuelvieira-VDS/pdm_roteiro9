package com.example.compose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compose.domain.model.Task
import com.example.compose.domain.usecase.task.DeleteTask
import com.example.compose.domain.usecase.task.GetTask
import com.example.compose.domain.usecase.task.ObserveTasks
import com.example.compose.domain.usecase.task.SaveTask
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
class TaskViewModel @Inject constructor(
    observeTasks: ObserveTasks,
    private val getTask: GetTask,
    private val saveTask: SaveTask,
    private val deleteTask: DeleteTask
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    private val _validationErrors = MutableStateFlow<Set<ValidationField>>(emptySet())
    val validationErrors = _validationErrors.asStateFlow()
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    val tasks: StateFlow<List<Task>> = observeTasks()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    suspend fun getTaskById(id: String?): Task? = getTask(id)

    fun clearErrors() {
        _validationErrors.value = emptySet()
        _errorMessage.value = null
    }

    fun saveTask(task: Task, onComplete: () -> Unit = {}) = performAction {
        saveTask.invoke(task)
        onComplete()
    }

    fun deleteTask(id: String) = performAction { deleteTask.invoke(id) }

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
