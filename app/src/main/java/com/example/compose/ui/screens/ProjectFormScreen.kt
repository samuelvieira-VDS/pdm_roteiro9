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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Save
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.compose.model.Project
import com.example.compose.model.ProjectStatus
import com.example.compose.ui.components.CancelButton
import com.example.compose.ui.components.CustomTextField
import com.example.compose.ui.components.PrimaryButton
import com.example.compose.viewmodel.ProjectViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProjectFormScreen(
    projectId: String?,
    viewModel: ProjectViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLoading by viewModel.isLoading.collectAsState()

    var existingProject by remember { mutableStateOf<Project?>(null) }
    var isFetching by remember { mutableStateOf(!projectId.isNullOrBlank()) }

    var name by remember { mutableStateOf("") }
    var client by remember { mutableStateOf("") }
    var budgetText by remember { mutableStateOf("") }
    var status by remember { mutableStateOf(ProjectStatus.PLANEJAMENTO) }
    var description by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf(false) }
    var clientError by remember { mutableStateOf(false) }

    val isEditing = !projectId.isNullOrBlank()

    LaunchedEffect(projectId) {
        if (!projectId.isNullOrBlank()) {
            isFetching = true
            val project = viewModel.getProjectById(projectId)
            existingProject = project
            project?.let {
                name = it.name
                client = it.client
                budgetText = if (it.budget > 0) it.budget.toString() else ""
                status = it.status
                description = it.description
            }
            isFetching = false
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = if (isEditing) "Editar Projeto" else "Novo Projeto") },
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
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = false
                    },
                    label = "Nome do Projeto *",
                    icon = Icons.Default.Folder,
                    isError = nameError,
                    errorMessage = if (nameError) "O nome do projeto é obrigatório" else null
                )

                CustomTextField(
                    value = client,
                    onValueChange = {
                        client = it
                        clientError = false
                    },
                    label = "Cliente / Empresa *",
                    icon = Icons.Default.Business,
                    isError = clientError,
                    errorMessage = if (clientError) "O cliente é obrigatório" else null
                )

                CustomTextField(
                    value = budgetText,
                    onValueChange = { budgetText = it },
                    label = "Orçamento (R$)",
                    icon = Icons.Default.AttachMoney,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                CustomTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = "Descrição do Projeto",
                    icon = Icons.Default.Description,
                    singleLine = false
                )

                Text(
                    text = "Status do Projeto",
                    style = MaterialTheme.typography.titleMedium
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ProjectStatus.entries.forEach { item ->
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
                            val isNameValid = name.isNotBlank()
                            val isClientValid = client.isNotBlank()

                            nameError = !isNameValid
                            clientError = !isClientValid

                            if (isNameValid && isClientValid) {
                                val parsedBudget = budgetText.replace(",", ".").toDoubleOrNull() ?: 0.0
                                val projectToSave = Project(
                                    id = existingProject?.id ?: UUID.randomUUID().toString(),
                                    name = name.trim(),
                                    client = client.trim(),
                                    budget = parsedBudget,
                                    status = status,
                                    description = description.trim()
                                )
                                viewModel.saveProject(projectToSave) {
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
