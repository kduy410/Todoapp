package com.example.mytodo

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.example.mytodo.addedit.AddEditTaskViewModel
import com.example.mytodo.data.source.TasksRepository
import com.example.mytodo.statistics.StatisticsViewModel
import com.example.mytodo.taskdetail.TaskDetailViewModel
import com.example.mytodo.tasks.TasksViewModel
import javax.inject.Singleton

/**
 * Factory for all ViewModels.
 */
@Suppress("UNCHECKED_CAST")
@Singleton
class ViewModelFactory internal constructor(
    private val tasksRepository: TasksRepository,
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        return with(modelClass) {
            when {
                isAssignableFrom(TasksViewModel::class.java) -> TasksViewModel(
                    tasksRepository,
                    handle
                )
                isAssignableFrom(AddEditTaskViewModel::class.java) -> AddEditTaskViewModel(tasksRepository)
                isAssignableFrom(TaskDetailViewModel::class.java) -> TaskDetailViewModel(tasksRepository)
                isAssignableFrom(StatisticsViewModel::class.java) -> StatisticsViewModel(tasksRepository)
                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
    }

}