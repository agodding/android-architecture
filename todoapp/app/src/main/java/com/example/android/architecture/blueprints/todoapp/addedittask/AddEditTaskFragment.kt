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
package com.example.android.architecture.blueprints.todoapp.addedittask

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.util.showSnackbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.addtask_frag.*

/**
 * Main UI for the add task screen. Users can enter a task title and description.
 */
class AddEditTaskFragment : Fragment() {

    private lateinit var viewModel: AddEditTaskViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = (activity as AddEditTaskActivity).obtainViewModel()
        setupFab()
        setupActionBar()
        loadData()
        bindViewModel()
    }

    private fun loadData() {
        viewModel.start(arguments?.getString(ARGUMENT_EDIT_TASK_ID))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.addtask_frag, container, false)

        setHasOptionsMenu(true)
        retainInstance = false
        return root
    }

    private fun bindViewModel() {
        viewModel.apply {
            dataLoading.observe(this@AddEditTaskFragment, Observer {
                refresh_layout.isRefreshing = it
                refresh_layout.isEnabled = it
                container.visibility = if (it) View.GONE else View.VISIBLE
            })
            title.observe(this@AddEditTaskFragment, Observer {
                add_task_title.setText(it)
            })
            description.observe(this@AddEditTaskFragment, Observer {
                add_task_description.setText(it)
            })
            snackbarMessage.observe(this@AddEditTaskFragment, Observer {
                it.getContentIfNotHandled()?.let { stringRes ->
                    view?.showSnackbar(getString(stringRes), Snackbar.LENGTH_LONG)
                }
            })
        }
    }

    private fun setupFab() {
        activity?.findViewById<FloatingActionButton>(R.id.fab_edit_task_done)?.let {
            it.setImageResource(R.drawable.ic_done)
            it.setOnClickListener {
                viewModel.saveTask(add_task_title.text.toString(), add_task_description.text.toString())
            }
        }
    }

    private fun setupActionBar() {
        (activity as AppCompatActivity).supportActionBar?.setTitle(
                if (arguments != null && arguments?.get(ARGUMENT_EDIT_TASK_ID) != null)
                    R.string.edit_task
                else
                    R.string.add_task
        )
    }

    companion object {
        const val ARGUMENT_EDIT_TASK_ID = "EDIT_TASK_ID"

        fun newInstance() = AddEditTaskFragment()
    }
}
