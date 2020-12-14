package com.example.mytodo.tasks

import android.graphics.Paint
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mytodo.R
import com.example.mytodo.data.Task

/**
 * [BindingAdapter]s for the [Task]s list.
 */

@BindingAdapter("app:items")
fun setItems(listView: RecyclerView, items: List<Task>?) {
    items?.let {
        (listView.adapter as TasksAdapter).submitList(items)
    }
}

@BindingAdapter("app:completedTask")
fun setStyle(textView: TextView, enabled: Boolean) {
    if (enabled) {
        textView.apply {
            paintFlags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        }

    } else {
        textView.paintFlags = textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
    }
}

@BindingAdapter("app:completedStatus")
fun setBackgroundColor(viewGroup: ViewGroup, enabled: Boolean) {
    if (enabled) {
        viewGroup.apply {
            background = ContextCompat.getDrawable(context, R.drawable.custom_ripple_completed)
        }
    } else {
        viewGroup.apply {
            background = ContextCompat.getDrawable(context, R.drawable.custom_ripple)
        }
    }
}