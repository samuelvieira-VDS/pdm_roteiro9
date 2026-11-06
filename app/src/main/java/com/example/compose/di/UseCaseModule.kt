package com.example.compose.di

import com.example.compose.domain.repository.TaskRepository
import com.example.compose.domain.usecase.task.DeleteTask
import com.example.compose.domain.usecase.task.GetTask
import com.example.compose.domain.usecase.task.ObserveTasks
import com.example.compose.domain.usecase.task.SaveTask
import com.example.compose.domain.repository.ProjectRepository
import com.example.compose.domain.usecase.project.DeleteProject
import com.example.compose.domain.usecase.project.GetProject
import com.example.compose.domain.usecase.project.ObserveProjects
import com.example.compose.domain.usecase.project.SaveProject
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides
    fun provideObserveTasks(repository: TaskRepository): ObserveTasks = ObserveTasks(repository)

    @Provides
    fun provideGetTask(repository: TaskRepository): GetTask = GetTask(repository)

    @Provides
    fun provideSaveTask(repository: TaskRepository): SaveTask = SaveTask(repository)

    @Provides
    fun provideDeleteTask(repository: TaskRepository): DeleteTask = DeleteTask(repository)

    @Provides
    fun provideObserveProjects(repository: ProjectRepository): ObserveProjects = ObserveProjects(repository)

    @Provides
    fun provideGetProject(repository: ProjectRepository): GetProject = GetProject(repository)

    @Provides
    fun provideSaveProject(repository: ProjectRepository): SaveProject = SaveProject(repository)

    @Provides
    fun provideDeleteProject(repository: ProjectRepository): DeleteProject = DeleteProject(repository)
}
