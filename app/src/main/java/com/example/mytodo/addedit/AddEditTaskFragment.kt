package com.example.mytodo.addedit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mytodo.EventObserver
import com.example.mytodo.R
import com.example.mytodo.databinding.AddEditTaskFragmentBinding
import com.example.mytodo.tasks.ADD_EDIT_RESULT_OK
import com.example.mytodo.util.getViewModelFactory
import com.example.mytodo.util.hideKeyboardFrom
import com.example.mytodo.util.setupRefreshLayout
import com.example.mytodo.util.setupSnackBar
import com.google.android.material.snackbar.Snackbar

/**
 * Main UI for the add task screen. Users can enter a task title and description.
 */
class AddEditTaskFragment : Fragment() {

    private lateinit var viewDataBinding: AddEditTaskFragmentBinding

    private val args: AddEditTaskFragmentArgs by navArgs()

    private val viewModel by viewModels<AddEditTaskViewModel> { getViewModelFactory() }

    private lateinit var addTaskTitleEditText: EditText

    private lateinit var addTaskDescriptionEditText: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /**
         * https://developer.android.com/topic/libraries/data-binding/two-way
         * 2 way data binding will automatically restore state of edit each time
         * the screen rotates
         */
//        if (savedInstanceState != null) {
//            viewModel.restoreState(
//                savedInstanceState.getString(TITLE_KEY).toString(),
//                savedInstanceState.getString(DESCRIPTION_KEY).toString()
//            )
//        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Use 1
//        val root = inflater.inflate(R.layout.add_edit_task_fragment,container,false)
//        viewDataBinding = AddEditTaskFragmentBinding.bind(root).apply {
//            addEditViewModel = viewModel
//        }
        viewDataBinding = AddEditTaskFragmentBinding.inflate(inflater, container, false)
            .apply {
                this.addEditViewModel = viewModel
                // Set the lifecycle owner to the lifecycle of the view
                this.lifecycleOwner = this@AddEditTaskFragment.viewLifecycleOwner
            }

        return viewDataBinding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TITLE_KEY, addTaskTitleEditText.text.toString())
        outState.putString(DESCRIPTION_KEY, addTaskDescriptionEditText.text.toString())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpEditText()
        setUpSnackBar()
        setUpNavigation()
        this.setupRefreshLayout(viewDataBinding.refreshLayout)
        viewModel.start(args.taskId)
    }

    private fun setUpNavigation() {
        viewModel.taskUpdatedEvent.observe(this.viewLifecycleOwner, EventObserver {
            // Doesn't apply argument??
            // Fixed : https://stackoverflow.com/questions/57029081/androidx-navigation-too-many-arguments-for-nonnull-public-open-fun
            // probably need to use the Kotlin version of the plugin like this
            // apply plugin: "androidx.navigation.safeargs.kotlin".
            val action =
                AddEditTaskFragmentDirections.actionAddEditTaskFragmentDestToTasksFragmentDest(
                    ADD_EDIT_RESULT_OK
                )
            findNavController().navigate(action)
        })
    }

    private fun setUpSnackBar() {
        view?.setupSnackBar(this, viewModel.snackBarText, Snackbar.LENGTH_SHORT)
    }

    private fun setOnFocusChangeListener(vararg editTexts: EditText) =
        editTexts.map {
            it.setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
                    hideKeyboardFrom(context, v)
                }
            }
        }


    private fun setUpEditText() {
        addTaskTitleEditText = view?.findViewById(R.id.add_task_title_edit_text)!!
        addTaskDescriptionEditText = view?.findViewById(R.id.add_task_description_edit_text)!!
        setOnFocusChangeListener(addTaskTitleEditText, addTaskDescriptionEditText)
    }

}