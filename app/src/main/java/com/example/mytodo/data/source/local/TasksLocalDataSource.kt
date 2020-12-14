package com.example.mytodo.data.source.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.mytodo.data.Result
import com.example.mytodo.data.Task
import com.example.mytodo.data.source.TasksDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Concrete implementation of a data source as a db.
 */
@Singleton
class TasksLocalDataSource internal constructor(
    private val tasksDAO: TasksDAO,
) : TasksDataSource {
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    override fun observeTasks(): LiveData<Result<List<Task>>> {
        return tasksDAO.observeTasks().map {
            Result.Success(it)
        }
    }

    override suspend fun getTasks(): Result<List<Task>> {
        return withContext(ioDispatcher) {
            return@withContext try {
                Result.Success(tasksDAO.getTasks())
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    override suspend fun refreshTasks() {
        // Some - thing
    }

    override fun observeTask(taskId: String): LiveData<Result<Task>> {
        return tasksDAO.observeTaskById(taskId).map {
            Result.Success(it)
        }
    }

    override suspend fun getTask(taskId: String): Result<Task> {
        return withContext(ioDispatcher) {
            try {
                val task = tasksDAO.getTaskById(taskId)
                if (task != null) {
                    return@withContext Result.Success(task)
                } else {
                    return@withContext Result.Error(java.lang.Exception("Task not found!"))
                }
            } catch (e: java.lang.Exception) {
                return@withContext Result.Error(e)
            }
        }
    }

    override suspend fun refreshTask(taskId: String) {
        // Some - thing
    }

    override suspend fun saveTask(task: Task) {
        tasksDAO.insertTask(task)
    }

    override suspend fun completeTask(task: Task) {
        withContext(ioDispatcher) {
            tasksDAO.updateCompleted(taskId = task.id, completed = true)
        }
    }

    override suspend fun completeTask(taskId: String) {
        withContext(ioDispatcher) {
            tasksDAO.updateCompleted(taskId = taskId, completed = true)
        }
    }

    override suspend fun activateTask(task: Task) {
        withContext(ioDispatcher) {
            tasksDAO.updateCompleted(taskId = task.id, completed = false)
        }
    }

    override suspend fun activateTask(taskId: String) {
        tasksDAO.updateCompleted(taskId = taskId, completed = false)
    }

    override suspend fun clearCompletedTasks() {
        withContext<Unit>(ioDispatcher) {
            tasksDAO.deleteCompletedTasks()
        }
    }

    override suspend fun deleteAllTasks() {
        withContext(ioDispatcher) {
            tasksDAO.deleteTasks()
        }
    }

    override suspend fun deleteTask(taskId: String) {
        withContext<Unit>(ioDispatcher) {
            tasksDAO.deleteTaskById(taskId)
        }
    }
}