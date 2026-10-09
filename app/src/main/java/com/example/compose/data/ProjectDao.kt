package com.example.compose.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.compose.model.Project
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects")
    fun getAllProjects(): Flow<List<Project>>

    @Query("SELECT * FROM projects WHERE id = :id")
    suspend fun getProjectById(id: String): Project?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProject(project: Project): Long

    @Delete
    suspend fun deleteProject(project: Project): Int
}
