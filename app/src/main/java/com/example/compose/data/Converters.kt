package com.example.compose.data

import androidx.room.TypeConverter
import com.example.compose.model.Priority
import com.example.compose.model.ProjectStatus
import com.example.compose.model.TaskStatus

class Converters {
    @TypeConverter
    fun fromProjectStatus(value: ProjectStatus): String = value.name

    @TypeConverter
    fun toProjectStatus(value: String): ProjectStatus = ProjectStatus.valueOf(value)

    @TypeConverter
    fun fromPriority(value: Priority): String = value.name

    @TypeConverter
    fun toPriority(value: String): Priority = Priority.valueOf(value)

    @TypeConverter
    fun fromTaskStatus(value: TaskStatus): String = value.name

    @TypeConverter
    fun toTaskStatus(value: String): TaskStatus = TaskStatus.valueOf(value)
}
