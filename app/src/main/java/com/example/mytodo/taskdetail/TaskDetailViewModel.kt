package com.example.mytodo.taskdetail

import androidx.annotation.StringRes
import androidx.lifecycle.*
import com.example.mytodo.Event
import com.example.mytodo.R
import com.example.mytodo.data.Result
import com.example.mytodo.data.Task
import com.example.mytodo.data.source.TasksRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for the Details screen.
 */
class TaskDetailViewModel(private val repository: TasksRepository) : ViewModel() {

    private val _taskId = MutableLiveData<String>()

    private val _task = _taskId.switchMap { taskId ->
        repository.observeTask(taskId).map {
            computeResult(it)
        }
    }

    val task: LiveData<Task?> = _task

    val isDataAvailable: LiveData<Boolean> = _task.map {
        it != null
    }

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _editTaskEvent = MutableLiveData<Event<Unit>>()
    val editTaskEvent: LiveData<Event<Unit>> = _editTaskEvent

    private val _deleteTaskEvent = MutableLiveData<Event<Unit>>()
    val deleteTaskEvent: LiveData<Event<Unit>> = _deleteTaskEvent

    private val _snackBarText = MutableLiveData<Event<Int>>()
    val snackBarText: LiveData<Event<Int>> = _snackBarText

    // This LiveData depends on another so we can use a transformation.
    val completed: LiveData<Boolean> = _task.map { task: Task? ->
        task?.isCompleted ?: false
    }

    fun deleteTask() = viewModelScope.launch {
        _taskId.value?.let {
            repository.deleteTask(it)
            _deleteTaskEvent.value = Event(Unit)
        }
    }

    fun editTask() {
        _editTaskEvent.value = Event(Unit)
    }

    fun setCompleted(completed: Boolean) {
        viewModelScope.launch {
            val task = _task.value ?: return@launch
            if (completed) {
                repository.completeTask(task)
                showSnackBarMessage(R.string.task_marked_complete)
            } else {
                repository.activateTask(task)
                showSnackBarMessage(R.string.task_marked_active)
            }
        }
    }

    fun start(taskId: String?) {
        // If we're already loading or already loaded, return (might be a config change)
        if (_dataLoading.value == true || taskId == _taskId.value) {
            return
        }
        // Trigger the load
        _taskId.value = taskId!!
    }

    private fun computeResult(result: Result<Task>): Task? {
        return if (result is Result.Success) {
            result.data
        } else {
            showSnackBarMessage(R.string.loading_task_error)
            return null
        }
    }

    private fun showSnackBarMessage(@StringRes message: Int) {
        _snackBarText.value = Event(message)
    }

    fun refresh(){
        // Refresh the repository and the task will be updated automatically.
        _task.value?.let {
            _dataLoading.value = true
            viewModelScope.launch {
                repository.refreshTask(taskId = it.id)
                _dataLoading.value = false
            }
        }
    }
}