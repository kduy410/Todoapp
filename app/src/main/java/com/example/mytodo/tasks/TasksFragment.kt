package com.example.mytodo.tasks

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mytodo.Event
import com.example.mytodo.EventObserver
import com.example.mytodo.R
import com.example.mytodo.databinding.TasksFragBinding
import com.example.mytodo.util.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import timber.log.Timber

/**
 * Display a grid of [Task]s. User can choose to view all, active or completed tasks.
 */
class TasksFragment : Fragment() {

    private val viewModel by viewModels<TasksViewModel> { getViewModelFactory() }

    private val args: TasksFragmentArgs by navArgs()

    private lateinit var viewDataBinding: TasksFragBinding

    private lateinit var listAdapter: TasksAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        viewDataBinding = TasksFragBinding.inflate(inflater, container, false).apply {
            tasksFragViewModel = viewModel
            // Set the lifecycle owner to the lifecycle of the view
            this.lifecycleOwner = this@TasksFragment.viewLifecycleOwner
        }
        setHasOptionsMenu(true)
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSnackBar()
        setupListAdapter()
        setupRefreshLayout(viewDataBinding.refreshLayout, viewDataBinding.tasksList)
        setupNavigation()
        setupFab()

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.tasks_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_clear -> {
                viewModel.clearCompletedTasks()
                true
            }
            R.id.menu_filter -> {
                showFilteringPopUpMenu()
                true
            }
            R.id.menu_refresh -> {
                viewModel.loadTasks(true)
                true
            }
            else -> false
        }
    }

    private fun showFilteringPopUpMenu() {
        // ?: return -> structural jump expressions, https://kotlinlang.org/docs/reference/returns.html
        // https://stackoverflow.com/questions/48310136/why-can-return-return-a-return-in-kotlin
        // "return" below here is an "expression" in Kotlin
        // with a return type of Nothing
        // if(a!=null) a else b
        val view = activity?.findViewById<View>(R.id.menu_filter) ?: return

        // Used context theme wrapper for background popup menu of this
        // or just apply some style

        PopupMenu(ContextThemeWrapper(requireContext(), R.style.PopupTheme), view).run {
            menuInflater.inflate(R.menu.filter_tasks, menu)
            setOnMenuItemClickListener {
                viewModel.setFiltering(
                    when (it.itemId) {
                        R.id.active -> TasksFilterType.ACTIVE_TASKS
                        R.id.completed -> TasksFilterType.COMPLETED_TASKS
                        else -> TasksFilterType.ALL_TASKS
                    }
                )
                true
            }
            show()
        }
    }

    private fun setupFab() {
        // It's must be view? -> not activity?
        // Because activity only find FAB after onActivityCreated -> if it's before onActivityCreated -> FAB is Null
        // Replace with view -> in onViewCreated -> this run just before onActivityCreated
        val fab = view?.findViewById<FloatingActionButton>(R.id.add_task_fab)
        fab.apply {
            this?.setOnClickListener {
                navigateToAddNewTask()
            }
        }
    }

    private fun navigateToAddNewTask() {
        val action = TasksFragmentDirections.actionTasksFragmentDestToAddEditTaskFragmentDest(
            resources.getString(R.string.add_task),
            null
        )
        findNavController().navigate(action)
    }

    private fun setupNavigation() {
        /**
         * if one of these newTaskEvent or openTaskEvent's value changes
         * it will trigger these function
         */

        viewModel.openTaskEvent.observe(this.viewLifecycleOwner, EventObserver {
            openTaskDetails(it)
        })

        viewModel.newTaskEvent.observe(this.viewLifecycleOwner, EventObserver {
            navigateToAddNewTask()
        })
    }

    private fun openTaskDetails(taskId: String) {
        val action = TasksFragmentDirections.actionTasksFragmentDestToTaskDetailFragment(taskId)
        findNavController().navigate(action)
    }

    private fun setupListAdapter() {
        val viewModel = viewDataBinding.tasksFragViewModel
        if (viewModel != null) {
            listAdapter = TasksAdapter(viewModel)
            viewDataBinding.tasksList.adapter = listAdapter
        } else {
            Timber.w("ViewModel not initialized when attempting to set up adapter")
        }
    }

    private fun setupSnackBar() {
        view?.setupSnackBar(this, viewModel.snackBarText, Snackbar.LENGTH_SHORT)
        arguments?.let {
            // This is safe-args defined in navigation.xml
            viewModel.showEditResultMessage(args.userMessage)
        }
    }
}