package com.example.mytodo.statistics

import com.example.mytodo.data.Task

/**
 * Function that does some trivial computation. Used to showcase unit tests.
 */

internal fun getActiveAndCompletedStats(tasks: List<Task>?): StatsResult {
    return if (tasks == null || tasks.isEmpty()) {
        StatsResult(0f, 0f)
    } else {
        val total = tasks.size
        val numberOfActiveTasks = tasks.count { task: Task -> task.isActive }
        StatsResult(
            100f * numberOfActiveTasks / total,
            100f * (total - numberOfActiveTasks) / total
        )
    }
}

data class StatsResult(val activeTasksPercent: Float, val completedTaskPercent: Float)