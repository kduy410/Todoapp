package com.example.mytodo.data.source

import androidx.lifecycle.LiveData
import com.example.mytodo.data.Result
import com.example.mytodo.data.Task
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Default implementation of [TasksRepository]. Single entry point for managing tasks' data.
 *
 * Currently doesn't need Espresso yet
 */
@Singleton
class DefaultTasksRepository constructor(
    private val tasksRemoteDataSource: TasksDataSource,
    private val tasksLocalDataSource: TasksDataSource,
) : TasksRepository {

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    override fun observeTasks(): LiveData<Result<List<Task>>> {
        return tasksLocalDataSource.observeTasks()
    }

    override suspend fun getTasks(forceUpdate: Boolean): Result<List<Task>> {
        if (forceUpdate) {
            try {
                updateTasksFromRemoteDataSource()
            } catch (e: Exception) {
                return Result.Error(e)
            }
        }
        return tasksLocalDataSource.getTasks()
    }

    private suspend fun updateTasksFromRemoteDataSource() {
        /**
         * remoteTask doesn't exist yet
         */
        val remoteTasks = tasksRemoteDataSource.getTasks()

        if (remoteTasks is Result.Success) {
            // Real apps might want to do a proper sync, deleting, modifying or adding each task.
            tasksLocalDataSource.deleteAllTasks()
            remoteTasks.data.forEach { task ->
                Timber.e(task.toString().plus(" added"))
                tasksLocalDataSource.saveTask(task)
            }
        } else if (remoteTasks is Result.Error) {
            throw remoteTasks.exception
        }
    }

    private suspend fun updateTaskFromRemoteDataSource(taskId: String) {
        /**
         * remoteTask doesn't exist yet
         */
        val remoteTask = tasksRemoteDataSource.getTask(taskId)

        if (remoteTask is Result.Success) {
            // Real apps might want to do a proper sync, deleting, modifying or adding each task.
            tasksLocalDataSource.saveTask(remoteTask.data)
        } else if (remoteTask is Result.Error) {
            throw remoteTask.exception
        }
    }

    override suspend fun refreshTasks() {
        updateTasksFromRemoteDataSource()
    }

    override fun observeTask(taskId: String): LiveData<Result<Task>> {
        return tasksLocalDataSource.observeTask(taskId)
    }

    /**
     * Relies on [getTasks] to fetch data and picks the task with the same ID.
     */

    override suspend fun getTask(taskId: String, forceUpdate: Boolean): Result<Task> {
        if (forceUpdate) {
            updateTaskFromRemoteDataSource(taskId)
        }
        return tasksLocalDataSource.getTask(taskId)
    }

    override suspend fun refreshTask(taskId: String) {
        updateTaskFromRemoteDataSource(taskId)
    }

    override suspend fun saveTask(task: Task) {
        coroutineScope {
            launch {
                tasksRemoteDataSource.saveTask(task)
            }
            launch {
                tasksLocalDataSource.saveTask(task)
            }
        }
    }

    override suspend fun completeTask(task: Task) {
        coroutineScope {
            launch { tasksRemoteDataSource.completeTask(task) }
            launch { tasksLocalDataSource.completeTask(task) }
        }
    }

    override suspend fun completeTask(taskId: String) {
        withContext(ioDispatcher) {
            (getTaskWithId(taskId) as? Result.Success)?.let { it ->
                completeTask(it.data)
            }
        }
    }

    private suspend fun getTaskWithId(taskId: String): Result<Task> {
        return tasksLocalDataSource.getTask(taskId)
    }

    override suspend fun activateTask(task: Task) {
        withContext<Unit>(ioDispatcher) {
            coroutineScope {
                launch { tasksRemoteDataSource.activateTask(task) }
                launch { tasksLocalDataSource.activateTask(task) }
            }
        }

    }

    override suspend fun activateTask(taskId: String) {
        withContext(ioDispatcher) {
            (getTaskWithId(taskId) as? Result.Success)?.let { it ->
                activateTask(it.data)
            }
        }
    }

    override suspend fun clearCompletedTasks() {
        coroutineScope {
            launch { tasksRemoteDataSource.clearCompletedTasks() }
            launch { tasksLocalDataSource.clearCompletedTasks() }
        }
    }

    override suspend fun deleteAllTasks() {
        withContext(ioDispatcher) {
            coroutineScope {
                launch { tasksRemoteDataSource.deleteAllTasks() }
                launch { tasksLocalDataSource.deleteAllTasks() }
            }
        }
    }

    override suspend fun deleteTask(taskId: String) {
        coroutineScope {
            launch { tasksRemoteDataSource.deleteTask(taskId) }
            launch { tasksLocalDataSource.deleteTask(taskId) }
        }
    }
}