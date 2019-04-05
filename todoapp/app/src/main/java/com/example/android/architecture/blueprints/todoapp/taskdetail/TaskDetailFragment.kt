/*
 * Copyright (C) 2017 The Android Open Source Project
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
package com.example.android.architecture.blueprints.todoapp.taskdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.util.setVisible
import com.example.android.architecture.blueprints.todoapp.util.setupSnackbar
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.taskdetail_frag.*

/**
 * Main UI for the task detail screen.
 */
class TaskDetailFragment : Fragment() {

    private lateinit var viewModel: TaskDetailViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = (activity as TaskDetailActivity).obtainViewModel()
        setupFab()
        bindViewModel()
    }

    override fun onResume() {
        super.onResume()
        viewModel.start(arguments?.getString(ARGUMENT_TASK_ID))
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.taskdetail_frag, container, false)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.taskdetail_fragment_menu, menu)
    }

    private fun bindViewModel() {
        viewModel.apply {
            dataLoading.observe(this@TaskDetailFragment, Observer {
                refresh_layout.isRefreshing = it
            })
            isDataAvailable.observe(this@TaskDetailFragment, Observer {
                no_data_container.setVisible(!it)
                task_detail_container.setVisible(it)
            })
            task.observe(this@TaskDetailFragment, Observer {
                task_detail_title.text = it.title
                task_detail_description.text = it.description
            })
            completed.observe(this@TaskDetailFragment, Observer {
                task_detail_complete.isChecked = it
            })
            view?.setupSnackbar(this@TaskDetailFragment, snackbarMessage, Snackbar.LENGTH_LONG)

            task_detail_complete.setOnClickListener { view ->
                viewModel.setCompleted((view as CheckBox).isChecked)
            }
            refresh_layout.setOnRefreshListener {
                viewModel.onRefresh()
            }
        }
    }

    private fun setupFab() {
        activity?.findViewById<View>(R.id.fab_edit_task)?.setOnClickListener {
            viewModel.editTask()
        }
    }

    companion object {

        const val ARGUMENT_TASK_ID = "TASK_ID"
        const val REQUEST_EDIT_TASK = 1

        fun newInstance(taskId: String) = TaskDetailFragment().apply {
            arguments = Bundle().apply {
                putString(ARGUMENT_TASK_ID, taskId)
            }
        }
    }
}
