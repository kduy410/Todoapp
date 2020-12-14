package com.example.mytodo.util

import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.mytodo.R
import com.example.mytodo.ScrollChildSwipeRefreshLayout
import com.example.mytodo.ToDoApplication
import com.example.mytodo.ViewModelFactory

/**
 * Extension functions for Fragment.
 */

fun Fragment.getViewModelFactory(): ViewModelFactory {
    val repository = (requireContext().applicationContext as ToDoApplication).tasksRepository
    return ViewModelFactory(repository, this)
}

fun Fragment.setupRefreshLayout(
    refreshLayout: ScrollChildSwipeRefreshLayout,
    scrollUpChild: View? = null
) {
    refreshLayout.setColorSchemeColors(
        ContextCompat.getColor(requireActivity(), R.color.colorPrimaryLight),
        ContextCompat.getColor(requireActivity(), R.color.colorPrimary),
        ContextCompat.getColor(requireActivity(), R.color.colorPrimaryDark)
    )

    // Set the scrolling view in the custom SwipeRefreshLayout.
    scrollUpChild?.let {
        refreshLayout.scrollUpChild = it
    }
    // Equal of
    // refreshLayout.scrollUpChild = scrollUpChild
}