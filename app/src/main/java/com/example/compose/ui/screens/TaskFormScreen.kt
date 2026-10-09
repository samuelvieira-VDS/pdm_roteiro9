package com.example.compose.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.compose.model.Priority
import com.example.compose.model.Task
import com.example.compose.model.TaskStatus
import com.example.compose.ui.components.CancelButton
import com.example.compose.ui.components.CustomTextField
import com.example.compose.ui.components.PrimaryButton
import com.example.compose.viewmodel.TaskViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TaskFormScreen(
    taskId: String?,
    viewModel: TaskViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLoading by viewModel.isLoading.collectAsState()

    var existingTask by remember { mutableStateOf<Task?>(null) }
    var isFetching by remember { mutableStateOf(!taskId.isNullOrBlank()) }

    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(Priority.MEDIA) }
    var status by remember { mutableStateOf(TaskStatus.PENDENTE) }
    var dueDate by remember { mutableStateOf("") }

    var titleError by remember { mutableStateOf(false) }
    var categoryError by remember { mutableStateOf(false) }

    val isEditing = !taskId.isNullOrBlank()

    LaunchedEffect(taskId) {
        if (!taskId.isNullOrBlank()) {
            isFetching = true
            val task = viewModel.getTaskById(taskId)
            existingTask = task
            task?.let {
                title = it.title
                category = it.category
                priority = it.priority
                status = it.status
                dueDate = it.dueDate
            }
            isFetching = false
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = if (isEditing) "Editar Tarefa" else "Nova Tarefa") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        if (isFetching || isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CustomTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        titleError = false
                    },
                    label = "Título da Tarefa *",
                    icon = Icons.Default.Title,
                    isError = titleError,
                    errorMessage = if (titleError) "O título é obrigatório" else null
                )

                CustomTextField(
                    value = category,
                    onValueChange = {
                        category = it
                        categoryError = false
                    },
                    label = "Categoria *",
                    icon = Icons.Default.Category,
                    isError = categoryError,
                    errorMessage = if (categoryError) "A categoria é obrigatória" else null
                )

                CustomTextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = "Data de Conclusão (ex: 20/11/2026)",
                    icon = Icons.Default.CalendarToday
                )

                Text(
                    text = "Prioridade",
                    style = MaterialTheme.typography.titleMedium
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Priority.entries.forEach { item ->
                        FilterChip(
                            selected = priority == item,
                            onClick = { priority = item },
                            label = { Text(item.label) }
                        )
                    }
                }

                Text(
                    text = "Status da Tarefa",
                    style = MaterialTheme.typography.titleMedium
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TaskStatus.entries.forEach { item ->
                        FilterChip(
                            selected = status == item,
                            onClick = { status = item },
                            label = { Text(item.label) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CancelButton(
                        text = "Cancelar",
                        onClick = onNavigateBack,
                        icon = Icons.Default.Cancel,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    PrimaryButton(
                        text = if (isEditing) "Atualizar" else "Salvar",
                        onClick = {
                            val isTitleValid = title.isNotBlank()
                            val isCategoryValid = category.isNotBlank()

                            titleError = !isTitleValid
                            categoryError = !isCategoryValid

                            if (isTitleValid && isCategoryValid) {
                                val taskToSave = Task(
                                    id = existingTask?.id ?: UUID.randomUUID().toString(),
                                    title = title.trim(),
                                    category = category.trim(),
                                    priority = priority,
                                    status = status,
                                    dueDate = dueDate.trim()
                                )
                                viewModel.saveTask(taskToSave) {
                                    onNavigateBack()
                                }
                            }
                        },
                        icon = Icons.Default.Save,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
