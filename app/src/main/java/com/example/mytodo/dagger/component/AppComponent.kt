package com.example.mytodo.dagger.component

import com.example.mytodo.ToDoApplication
import com.example.mytodo.dagger.module.DatabaseModule
import com.example.mytodo.dagger.module.RepositoryModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton


/**
 * Component is stand for the "THE INJECTOR"
 */
@Component(modules = [RepositoryModule::class, DatabaseModule::class])
@Singleton
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: ToDoApplication): Builder

        fun build(): AppComponent
    }

    /*
     * This is our custom Application class
     * */
    fun inject(application: ToDoApplication)
}