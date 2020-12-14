package com.example.mytodo.dagger.module

import android.app.Application
import androidx.annotation.NonNull
import androidx.room.Room
import com.example.mytodo.ToDoApplication
import com.example.mytodo.data.source.local.TasksDAO
import com.example.mytodo.data.source.local.ToDoDatabase
import dagger.Module
import dagger.Provides
import timber.log.Timber
import javax.inject.Singleton

@Module
class DatabaseModule {
    /*
     * The method returns the Database object
     * */
    @Provides
    @Singleton
    fun provideDatabase(@NonNull application: ToDoApplication): ToDoDatabase {
        Timber.e("Database provided...")
        return Room.databaseBuilder(application, ToDoDatabase::class.java, "Database.db")
            .allowMainThreadQueries().build()
    }

    /*
     * We need the TasksDAO module.
     * For this, We need the TodoDatabase object
     * So we will define the providers for this here in this module.
     * */
    @Provides
    @Singleton
    fun provideTasksDAO(@NonNull database: ToDoDatabase): TasksDAO {
        Timber.e("DAO provided...")
        return database.getTasksDao()
    }

}