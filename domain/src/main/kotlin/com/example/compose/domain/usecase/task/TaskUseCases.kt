package com.example.compose.domain.usecase.task

import com.example.compose.domain.model.Task
import com.example.compose.domain.repository.TaskRepository
import com.example.compose.domain.validation.ValidationException
import com.example.compose.domain.validation.ValidationField
import kotlinx.coroutines.flow.Flow

class ObserveTasks(private val repository: TaskRepository) {
    operator fun invoke(): Flow<List<Task>> = repository.allTasks
}

class GetTask(private val repository: TaskRepository) {
    suspend operator fun invoke(id: String?): Task? =
        if (id.isNullOrBlank()) null else repository.getTaskById(id)
}

class SaveTask(private val repository: TaskRepository) {
    suspend operator fun invoke(task: Task) {
        val errors = buildSet {
            if (task.title.isBlank()) add(ValidationField.TASK_TITLE)
            if (task.category.isBlank()) add(ValidationField.TASK_CATEGORY)
        }
        if (errors.isNotEmpty()) throw ValidationException(errors)
        repository.upsertTask(task.copy(title = task.title.trim(), category = task.category.trim(), dueDate = task.dueDate.trim()))
    }
}

class DeleteTask(private val repository: TaskRepository) {
    suspend operator fun invoke(id: String) {
        if (id.isBlank()) return
        repository.getTaskById(id)?.let { repository.deleteTask(it) }
    }
}
