/*
 * Copyright 2016, The Android Open Source Project
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

package com.example.android.architecture.blueprints.todoapp.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.util.setVisibility
import com.example.android.architecture.blueprints.todoapp.util.showSnackbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.tasks_frag.filteringLabel
import kotlinx.android.synthetic.main.tasks_frag.noTasks
import kotlinx.android.synthetic.main.tasks_frag.noTasksAdd
import kotlinx.android.synthetic.main.tasks_frag.noTasksMain
import kotlinx.android.synthetic.main.tasks_frag.populated_tasks
import kotlinx.android.synthetic.main.tasks_frag.refresh_layout
import kotlinx.android.synthetic.main.tasks_frag.tasks_list
import java.util.ArrayList

/**
 * Display a grid of [Task]s. User can choose to view all, active or completed tasks.
 */
class TasksFragment : Fragment() {

    private lateinit var listAdapter: TasksAdapter
    private lateinit var viewModel: TasksViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.tasks_frag, container, false)
    }

    override fun onResume() {
        super.onResume()
        viewModel.start()
    }

    override fun onOptionsItemSelected(item: MenuItem) =
            when (item.itemId) {
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.tasks_fragment_menu, menu)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = (activity as TasksActivity).obtainViewModel()
        bindViewModel()
        setupFab()
        setupListAdapter()
        setupRefreshLayout()
    }

    private fun bindViewModel() {
        viewModel.apply {
            snackbarMessage.observe(this@TasksFragment, Observer { event ->
                event.getContentIfNotHandled()?.let {
                    view?.showSnackbar(getString(it), Snackbar.LENGTH_LONG)
                }
            })
            dataLoading.observe(this@TasksFragment, Observer {
                refresh_layout.isRefreshing = it
            })
            empty.observe(this@TasksFragment, Observer {
                populated_tasks.setVisibility(!it)
                noTasks.setVisibility(it)
            })
            tasksAddViewVisible.observe(this@TasksFragment, Observer {
                noTasksAdd.setVisibility(it)
            })
            currentFilteringLabel.observe(this@TasksFragment, Observer {
                filteringLabel.text = getString(it)
            })
            noTaskIconRes.observe(this@TasksFragment, Observer {
                ContextCompat.getDrawable(context!!, it)
            })
            items.observe(this@TasksFragment, Observer {
                listAdapter.replaceData(it)
            })
            noTasksLabel.observe(this@TasksFragment, Observer {
                noTasksMain.text = getString(it)
            })
        }

        noTasksAdd.setOnClickListener {
            viewModel.addNewTask()
        }
    }

    private fun showFilteringPopUpMenu() {
        val view = activity?.findViewById<View>(R.id.menu_filter) ?: return
        PopupMenu(requireContext(), view).run {
            menuInflater.inflate(R.menu.filter_tasks, menu)

            setOnMenuItemClickListener {
                viewModel.run {
                    setFiltering(
                            when (it.itemId) {
                                R.id.active -> TasksFilterType.ACTIVE_TASKS
                                R.id.completed -> TasksFilterType.COMPLETED_TASKS
                                else -> TasksFilterType.ALL_TASKS
                            }
                    )
                    loadTasks(false)
                }
                true
            }
            show()
        }
    }

    private fun setupFab() {
        activity?.findViewById<FloatingActionButton>(R.id.fab_add_task)?.let {
            it.setImageResource(R.drawable.ic_add)
            it.setOnClickListener {
                viewModel.addNewTask()
            }
        }
    }

    private fun setupListAdapter() {
        listAdapter = TasksAdapter(ArrayList(0), viewModel)
        tasks_list.adapter = listAdapter
    }

    private fun setupRefreshLayout() {
        refresh_layout.run {
            setColorSchemeColors(
                    ContextCompat.getColor(requireActivity(), R.color.colorPrimary),
                    ContextCompat.getColor(requireActivity(), R.color.colorAccent),
                    ContextCompat.getColor(requireActivity(), R.color.colorPrimaryDark)
            )
            // Set the scrolling view in the custom SwipeRefreshLayout.
            scrollUpChild = tasks_list
            setOnRefreshListener {
                viewModel.loadTasks(true)
            }
        }
    }

    companion object {
        fun newInstance() = TasksFragment()
        private const val TAG = "TasksFragment"

    }
}
