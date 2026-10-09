package com.example.compose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compose.data.TaskRepository
import com.example.compose.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val tasks: StateFlow<List<Task>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    suspend fun getTaskById(id: String?): Task? {
        if (id.isNullOrBlank()) return null
        return repository.getTaskById(id)
    }

    fun saveTask(task: Task, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.upsertTask(task)
            _isLoading.value = false
            onComplete()
        }
    }

    fun deleteTask(id: String) {
        viewModelScope.launch {
            val task = repository.getTaskById(id)
            task?.let {
                _isLoading.value = true
                repository.deleteTask(it)
                _isLoading.value = false
            }
        }
    }
}
