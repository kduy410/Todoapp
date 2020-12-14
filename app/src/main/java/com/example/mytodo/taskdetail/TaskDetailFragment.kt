package com.example.mytodo.taskdetail

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mytodo.EventObserver
import com.example.mytodo.R
import com.example.mytodo.databinding.TaskDetailFragmentBinding
import com.example.mytodo.tasks.DELETE_RESULT_OK
import com.example.mytodo.util.getViewModelFactory
import com.example.mytodo.util.setupRefreshLayout
import com.example.mytodo.util.setupSnackBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

/**
 * Main UI for the task detail screen.
 */
class TaskDetailFragment : Fragment() {

    private val viewModel by viewModels<TaskDetailViewModel> { getViewModelFactory() }

    private val args: TaskDetailFragmentArgs by navArgs()

    private lateinit var viewDataBinding: TaskDetailFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.task_detail_fragment, container, false)
        viewDataBinding = TaskDetailFragmentBinding.bind(view).apply {
            this.taskDetailViewModel = viewModel
            this.lifecycleOwner = this@TaskDetailFragment.viewLifecycleOwner
        }
        viewModel.start(args.taskId)

        setHasOptionsMenu(true)

        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.setupSnackBar(this, viewModel.snackBarText, Snackbar.LENGTH_SHORT)
        this.setupRefreshLayout(viewDataBinding.refreshLayout)
        setupNavigation()
        setupFab()
    }

    private fun setupNavigation() {
        viewModel.deleteTaskEvent.observe(this.viewLifecycleOwner, EventObserver {
            val action = TaskDetailFragmentDirections.actionTaskDetailFragmentToTasksFragmentDest(
                DELETE_RESULT_OK
            )
            findNavController().navigate(action)
        })
        viewModel.editTaskEvent.observe(this.viewLifecycleOwner, EventObserver {
            val action =
                TaskDetailFragmentDirections.actionTaskDetailFragmentToAddEditTaskFragmentDest(
                    title = resources.getString(R.string.edit_task),
                    taskId = args.taskId
                )
            findNavController().navigate(action)
        })
    }

    private fun setupFab() {
        view?.findViewById<FloatingActionButton>(R.id.edit_task_fab)?.setOnClickListener {
            viewModel.editTask()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.taskdetail_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_delete -> {
                viewModel.deleteTask()
                true
            }
            else -> false
        }
    }

}