package com.example.mytodo.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mytodo.data.Task

@Database(entities = [Task::class], version = 1, exportSchema = false)
abstract class ToDoDatabase : RoomDatabase() {
    abstract fun getTasksDao(): TasksDAO
}