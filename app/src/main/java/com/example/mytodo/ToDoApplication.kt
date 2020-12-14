package com.example.mytodo

import android.app.Application
import com.example.mytodo.dagger.component.DaggerAppComponent
import com.example.mytodo.data.Task
import com.example.mytodo.data.source.TasksRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext


/**
 * An application that lazily provides a repository. Note that this Service Locator pattern is
 * used to simplify the sample. Consider a Dependency Injection framework.
 *
 * Also, sets up Timber in the DEBUG BuildConfig. Read Timber's documentation for production setups.
 */

@Suppress("UNCHECKED_CAST")
@Singleton
class ToDoApplication :  Application() {
    // Inject field
    @Inject
    lateinit var tasksRepository: TasksRepository

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
        DaggerAppComponent.builder().application(this).build().inject(this)

    }
}