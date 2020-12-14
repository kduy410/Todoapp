package com.example.mytodo.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.mytodo.R
import com.example.mytodo.databinding.StatisticsFragBinding
import com.example.mytodo.util.getViewModelFactory
import com.example.mytodo.util.setupRefreshLayout

/**
 * Main UI for the statistics screen.
 */
class StatisticsFragment : Fragment() {

    private val viewModel by viewModels<StatisticsViewModel> { getViewModelFactory() }

    private lateinit var viewDataBinding: StatisticsFragBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewDataBinding =
            DataBindingUtil.inflate(inflater, R.layout.statistics_frag, container, false)
        viewDataBinding.apply {
            this.statisticsTaskViewModel = viewModel
            this.lifecycleOwner = this@StatisticsFragment.viewLifecycleOwner
        }

        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.setupRefreshLayout(viewDataBinding.refreshLayout)
    }

}