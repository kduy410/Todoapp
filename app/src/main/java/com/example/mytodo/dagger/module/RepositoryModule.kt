package com.example.mytodo.dagger.module

import androidx.annotation.NonNull
import com.example.mytodo.data.source.DefaultTasksRepository
import com.example.mytodo.data.source.TasksDataSource
import com.example.mytodo.data.source.TasksRepository
import com.example.mytodo.data.source.local.TasksDAO
import com.example.mytodo.data.source.local.TasksLocalDataSource
import dagger.Module
import dagger.Provides
import timber.log.Timber
import javax.inject.Singleton

@Module
class RepositoryModule {
    /**
     *
     */
    @Provides
    @Singleton
    fun provideLocalRepository(@NonNull tasksDAO: TasksDAO): TasksDataSource {
        Timber.e("Local Repository provided...")
        return TasksLocalDataSource(tasksDAO)
    }

    /**
     * [remote] does not exist yet, instead use Local as fake remote
     */
//    @Provides
//    fun provideRemoteRepository(tasksDAO: TasksDAO): TasksRemoteDataSource {
//        return TasksRemoteDataSource(tasksDAO)
//    }

    /**
     * [repository] must be specified for dagger to understand which class is it
     * in this case we specified [DefaultTasksRepository]
     */
    @Provides
    @Singleton
    fun provideRepository(
        @NonNull localDataSource: TasksDataSource,
        @NonNull remoteDataSource: TasksDataSource
    ): TasksRepository {
        Timber.e("Repository provided...")
        return DefaultTasksRepository(remoteDataSource, localDataSource)
    }
}