package com.example.mytodo.addedit

import androidx.lifecycle.*
import com.example.mytodo.Event
import com.example.mytodo.R
import com.example.mytodo.data.Result
import com.example.mytodo.data.Task
import com.example.mytodo.data.source.TasksRepository
import kotlinx.coroutines.launch
import timber.log.Timber

class AddEditTaskViewModel(
    private val repository: TasksRepository,
) : ViewModel() {

    // Two-way data binding, exposing MutableLiveData
    var title = MutableLiveData<String>()

    // Two-way data binding, exposing MutableLiveData
    val description = MutableLiveData<String>()

    private val _snackBarText = MutableLiveData<Event<Int>>()
    val snackBarText: LiveData<Event<Int>> = _snackBarText

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _taskUpdatedEvent = MutableLiveData<Event<Unit>>()
    val taskUpdatedEvent: LiveData<Event<Unit>> = _taskUpdatedEvent

    private var taskId: String? = null

    private var isNewTask: Boolean = false

    private var isDataLoaded = false

    private var taskCompleted = false


    // Called when clicking on save fab
    fun saveTask() {
        val currentTitle = title.value
        val currentDescription = description.value

        Timber.e(currentTitle.toString())
        Timber.e(currentDescription.toString())

        if (currentTitle == null || currentDescription == null) {
            _snackBarText.value = Event(R.string.empty_task_message)
            return
        }
        if (Task(currentTitle, currentDescription).isEmpty) {
            _snackBarText.value = Event(R.string.empty_task_message)
            return
        }

        val currentTaskId = taskId
        if (isNewTask || currentTaskId == null) {
            createTask(Task(currentTitle, currentDescription))
        } else {
            val task = Task(currentTitle, currentDescription, taskCompleted, currentTaskId)
            updateTask(task)
        }

    }

    private fun updateTask(task: Task) {
        if (isNewTask) {
            throw RuntimeException("updateTask() was called but task is new")
        }
        viewModelScope.launch {
            repository.saveTask(task)
            _taskUpdatedEvent.value = Event(Unit)
        }
    }

    private fun createTask(task: Task) {
        viewModelScope.launch {
            repository.saveTask(task)
            _taskUpdatedEvent.value = Event(Unit)
        }
    }

    fun restoreState(title: String?, description: String?) {
        viewModelScope.launch {
            this@AddEditTaskViewModel.title.value = title ?: ""
            this@AddEditTaskViewModel.description.value = description ?: ""
        }
    }

    fun start(taskId: String?) {

        if (_dataLoading.value == true) {
            return
        }

        this.taskId = taskId

        if (taskId == null) {
            // No need to populate -> it's a new task
            isNewTask = true
            return
        }

        if (isDataLoaded) {
            // No need to populate -> already have data
            return
        }

        isNewTask = false
        _dataLoading.value = true

        viewModelScope.launch {
            repository.getTask(taskId).let { result ->
                if (result is Result.Success) {
                    onTaskLoaded(result.data)
                } else {
                    onDataNotAvailable()
                }
            }
        }
    }

    private fun onDataNotAvailable() {
        _dataLoading.value = false
    }

    private fun onTaskLoaded(task: Task) {
        title.value = task.title
        description.value = task.description
        taskCompleted = task.isCompleted
        _dataLoading.value = false
        isDataLoaded = true
    }
}

const val TITLE_KEY = "TITLE_KEY"
const val DESCRIPTION_KEY = "DESCRIPTION_KEY"