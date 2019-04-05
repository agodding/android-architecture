/*
 * Copyright 2017, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.architecture.blueprints.todoapp.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.util.setVisible
import kotlinx.android.synthetic.main.statistics_frag.*

/**
 * Main UI for the statistics screen.
 */
class StatisticsFragment : Fragment() {

    private lateinit var statisticsViewModel: StatisticsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.statistics_frag, container,false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        statisticsViewModel = (activity as StatisticsActivity).obtainViewModel()
        bindViewModel()
    }

    override fun onResume() {
        super.onResume()
        statisticsViewModel.start()
    }

    private fun bindViewModel() {
        statisticsViewModel.apply {
            dataLoading.observe(this@StatisticsFragment, Observer {
                loading_label.setVisible(it)
                statistics.setVisible(!it)
            })
            empty.observe(this@StatisticsFragment, Observer {
                empty_tasks_label.setVisible(it)
                active_tasks_label.setVisible(!it)
                completed_tasks_label.setVisible(!it)
            })
            numberOfActiveTasks.observe(this@StatisticsFragment, Observer {
                active_tasks_label.text = resources.getString(R.string.statistics_active_tasks, it)
            })
            numberOfCompletedTasks.observe(this@StatisticsFragment, Observer {
                completed_tasks_label.text = resources.getString(R.string.statistics_completed_tasks, it)
            })
        }
    }

    companion object {
        fun newInstance() = StatisticsFragment()
    }
}
