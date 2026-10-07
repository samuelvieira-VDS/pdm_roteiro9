package com.example.compose.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.model.Priority
import com.example.compose.model.ProjectStatus
import com.example.compose.model.TaskStatus

@Composable
fun PriorityBadge(
    priority: Priority,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = when (priority) {
        Priority.BAIXA -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
        Priority.MEDIA -> Color(0xFFFFF3E0) to Color(0xFFE65100)
        Priority.ALTA -> Color(0xFFFFEBEE) to Color(0xFFC62828)
    }

    BadgeText(
        text = "Prioridade: ${priority.label}",
        backgroundColor = backgroundColor,
        textColor = textColor,
        modifier = modifier
    )
}

@Composable
fun TaskStatusBadge(
    status: TaskStatus,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = when (status) {
        TaskStatus.PENDENTE -> Color(0xFFFFF8E1) to Color(0xFFF57F17)
        TaskStatus.EM_ANDAMENTO -> Color(0xFFE3F2FD) to Color(0xFF1565C0)
        TaskStatus.CONCLUIDA -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
    }

    BadgeText(
        text = status.label,
        backgroundColor = backgroundColor,
        textColor = textColor,
        modifier = modifier
    )
}

@Composable
fun ProjectStatusBadge(
    status: ProjectStatus,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = when (status) {
        ProjectStatus.PLANEJAMENTO -> Color(0xFFEDE7F6) to Color(0xFF512DA8)
        ProjectStatus.EM_EXECUCAO -> Color(0xFFE3F2FD) to Color(0xFF1565C0)
        ProjectStatus.CONCLUIDO -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
        ProjectStatus.PAUSADO -> Color(0xFFFFEBEE) to Color(0xFFC62828)
    }

    BadgeText(
        text = status.label,
        backgroundColor = backgroundColor,
        textColor = textColor,
        modifier = modifier
    )
}

@Composable
private fun BadgeText(
    text: String,
    backgroundColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
