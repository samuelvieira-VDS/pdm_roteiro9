package com.example.compose.domain.validation

enum class ValidationField { TASK_TITLE, TASK_CATEGORY, PROJECT_NAME, PROJECT_CLIENT }

class ValidationException(val fields: Set<ValidationField>) : IllegalArgumentException("Invalid fields: $fields")
