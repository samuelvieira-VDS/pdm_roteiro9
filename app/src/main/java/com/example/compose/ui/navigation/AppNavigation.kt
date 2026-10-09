package com.example.compose.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.navArgument
import com.example.compose.TaskApplication
import com.example.compose.ui.screens.ProjectFormScreen
import com.example.compose.ui.screens.ProjectListScreen
import com.example.compose.ui.screens.TaskFormScreen
import com.example.compose.ui.screens.TaskListScreen
import com.example.compose.viewmodel.AppViewModelFactory
import com.example.compose.viewmodel.ProjectViewModel
import com.example.compose.viewmodel.TaskViewModel

@Composable
fun AppNavigation() {
    val context = LocalContext.current.applicationContext as TaskApplication
    val projectViewModel: ProjectViewModel = viewModel(
        factory = AppViewModelFactory(context.projectRepository, context.taskRepository)
    )
    val taskViewModel: TaskViewModel = viewModel(
        factory = AppViewModelFactory(context.projectRepository, context.taskRepository)
    )
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        Screen.TaskList.route,
        Screen.ProjectList.route
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            selected = currentRoute == screen.route,
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                screen.icon?.let {
                                    Icon(
                                        imageVector = it,
                                        contentDescription = screen.title
                                    )
                                }
                            },
                            label = {
                                screen.title?.let { Text(it) }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.TaskList.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.TaskList.route) {
                TaskListScreen(
                    viewModel = taskViewModel,
                    onNavigateToForm = { taskId ->
                        navController.navigate(Screen.TaskForm.createRoute(taskId))
                    }
                )
            }

            composable(Screen.ProjectList.route) {
                ProjectListScreen(
                    viewModel = projectViewModel,
                    onNavigateToForm = { projectId ->
                        navController.navigate(Screen.ProjectForm.createRoute(projectId))
                    }
                )
            }

            composable(
                route = Screen.TaskForm.route,
                arguments = listOf(
                    navArgument("taskId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val taskId = backStackEntry.arguments?.getString("taskId")
                TaskFormScreen(
                    taskId = taskId,
                    viewModel = taskViewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = Screen.ProjectForm.route,
                arguments = listOf(
                    navArgument("projectId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val projectId = backStackEntry.arguments?.getString("projectId")
                ProjectFormScreen(
                    projectId = projectId,
                    viewModel = projectViewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
