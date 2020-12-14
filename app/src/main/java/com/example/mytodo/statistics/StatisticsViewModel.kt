package com.example.mytodo.statistics

import androidx.lifecycle.*
import com.example.mytodo.data.Result
import com.example.mytodo.data.Task
import com.example.mytodo.data.source.TasksRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for the statistics screen.
 */
class StatisticsViewModel(private val repository: TasksRepository) : ViewModel() {

    private val tasks: LiveData<Result<List<Task>>> = repository.observeTasks()
    private val _dataLoading = MutableLiveData<Boolean>(false)
    val dataLoading: LiveData<Boolean> = _dataLoading
    private val stats: LiveData<StatsResult?> = tasks.map {
        if (it is Result.Success) {
            getActiveAndCompletedStats(it.data)
        } else {
            null
        }
    }

    val activeTasksPercent: LiveData<Float> = stats.map {
        it?.activeTasksPercent ?: 0f
    }

    val completedTasksPercent: LiveData<Float> = stats.map {
        it?.completedTaskPercent ?: 0f
    }

    val error: LiveData<Boolean> = tasks.map { it is Result.Error }
    val empty: LiveData<Boolean> = tasks.map { (it as? Result.Success)?.data.isNullOrEmpty() }

    fun refresh() {
        _dataLoading.value = true
        viewModelScope.launch {
            repository.refreshTasks()
            _dataLoading.value = false
        }
    }
}