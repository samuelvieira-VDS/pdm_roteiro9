package com.example.compose.viewmodel

import androidx.lifecycle.ViewModel
import com.example.compose.model.Priority
import com.example.compose.model.Task
import com.example.compose.model.TaskStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TaskViewModel : ViewModel() {
    private val _tasks = MutableStateFlow<List<Task>>(
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
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    fun getTaskById(id: String?): Task? {
        if (id.isNull_or_blank()) return null
        return _tasks.value.find { it.id == id }
    }

    fun saveTask(task: Task) {
        _tasks.update { currentList ->
            val index = currentList.indexOfFirst { it.id == task.id }
            if (index >= 0) {
                currentList.toMutableList().apply { set(index, task) }
            } else {
                currentList + task
            }
        }
    }

    fun deleteTask(id: String) {
        _tasks.update { currentList -> currentList.filter { it.id != id } }
    }
}

private fun String?.isNull_or_blank(): Boolean = this == null || this.isBlank()
