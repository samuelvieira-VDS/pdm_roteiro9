package com.example.compose.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.compose.model.Priority
import com.example.compose.model.Project
import com.example.compose.model.ProjectStatus
import com.example.compose.model.Task
import com.example.compose.model.TaskStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Project::class, Task::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .addCallback(AppDatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class AppDatabaseCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDatabase(database.projectDao(), database.taskDao())
                    }
                }
            }
        }

        suspend fun populateDatabase(projectDao: ProjectDao, taskDao: TaskDao) {
            projectDao.upsertProject(
                Project(
                    id = "1",
                    name = "Redesign do App Mobile",
                    client = "TechCorp Inc.",
                    budget = 25000.0,
                    status = ProjectStatus.EM_EXECUCAO,
                    description = "Modernização completa da UI/UX com Jetpack Compose."
                )
            )
            projectDao.upsertProject(
                Project(
                    id = "2",
                    name = "Sistema de Vendas Web",
                    client = "Mercado Global",
                    budget = 40000.0,
                    status = ProjectStatus.PLANEJAMENTO,
                    description = "Plataforma e-commerce B2B responsiva."
                )
            )
            projectDao.upsertProject(
                Project(
                    id = "3",
                    name = "Migração para Nuvem",
                    client = "Fintech Brasil",
                    budget = 18000.0,
                    status = ProjectStatus.CONCLUIDO,
                    description = "Migração de infraestrutura para serviços de nuvem."
                )
            )

            taskDao.upsertTask(
                Task(
                    id = "1",
                    title = "Criar protótipo da interface",
                    category = "Design",
                    priority = Priority.ALTA,
                    status = TaskStatus.EM_ANDAMENTO,
                    dueDate = "15/11/2026"
                )
            )
            taskDao.upsertTask(
                Task(
                    id = "2",
                    title = "Implementar autenticação",
                    category = "Backend",
                    priority = Priority.ALTA,
                    status = TaskStatus.PENDENTE,
                    dueDate = "20/11/2026"
                )
            )
            taskDao.upsertTask(
                Task(
                    id = "3",
                    title = "Revisar documentação da API",
                    category = "Documentação",
                    priority = Priority.BAIXA,
                    status = TaskStatus.CONCLUIDA,
                    dueDate = "10/11/2026"
                )
            )
        }
    }
}
