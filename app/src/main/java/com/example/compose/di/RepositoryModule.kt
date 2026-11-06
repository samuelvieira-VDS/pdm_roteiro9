package com.example.compose.di

import com.example.compose.domain.repository.TaskRepository
import com.example.compose.data.repository.RoomTaskRepository
import com.example.compose.domain.repository.ProjectRepository
import com.example.compose.data.repository.RoomProjectRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindTaskRepository(implementation: RoomTaskRepository): TaskRepository

    @Binds
    abstract fun bindProjectRepository(implementation: RoomProjectRepository): ProjectRepository
}
