package com.carin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.carin.R
import com.carin.adapter.UsersTabAdapter
import com.carin.domain.enums.Role
import com.carin.domain.enums.TypeUsers
import com.carin.viewmodels.UsersViewModel
import com.carin.viewmodels.events.UsersListEvent
import com.carin.viewmodels.states.UsersListState

class UsersTabFragment : Fragment() {

    private lateinit var adapter: UsersTabAdapter
    private lateinit var viewModel: UsersViewModel
    private var currentRole: Role? = null
    private var dataLoaded = false
    private lateinit var emptyTextView: TextView
    private lateinit var errorTextView: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.user_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        adapter = UsersTabAdapter(mutableListOf())
        recyclerView.adapter = adapter

        emptyTextView = view.findViewById(R.id.emptyTextView)
        errorTextView = view.findViewById(R.id.errorTextView)
        progressBar = view.findViewById(R.id.progressBar)

        // Obtain the ViewModel from the Activity's ViewModelProvider
        viewModel = ViewModelProvider(requireActivity())[UsersViewModel::class.java]

        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UsersListState.Loading -> {
                    if (state.role == currentRole) {
                        progressBar.visibility = View.VISIBLE
                        errorTextView.visibility = View.GONE
                    }
                }
                is UsersListState.Success -> {
                    if (state.role == currentRole) {
                        progressBar.visibility = View.GONE
                        errorTextView.visibility = View.GONE


                        if (state.users.isEmpty() && !state.isAppending) {
                            emptyTextView.visibility = View.VISIBLE
                            recyclerView.visibility = View.GONE
                        } else {
                            emptyTextView.visibility = View.GONE
                            recyclerView.visibility = View.VISIBLE

                            if (state.isAppending)
                                adapter.appendUsers(state.users)
                            else
                                adapter.updateUsers(state.users)
                        }

                        dataLoaded = true
                    }
                }
                is UsersListState.Error -> {
                    progressBar.visibility = View.GONE
                    errorTextView.visibility = View.VISIBLE
                }
            }
        }

        viewModel.searchQuery.observe(viewLifecycleOwner) {
            // Reload data based on the new search query
            viewModel.onEvent(UsersListEvent.LoadUsers(currentRole))
        }

        // Add scroll listener to the RecyclerView
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                // Load more items when the user scrolls to the bottom
                if (!recyclerView.canScrollVertically(1)) {
                    viewModel.onEvent(UsersListEvent.LoadMoreUsers(currentRole))
                }
            }
        })

        // Get the role from arguments and convert it to Role
        val typeUser = arguments?.getSerializable("role") as? TypeUsers
        currentRole = typeUser?.let { TypeUsers.toRole(it) }
    }

    override fun onResume() {
        super.onResume()
        if (!dataLoaded)
            viewModel.onEvent(UsersListEvent.LoadUsers(currentRole))
    }
}
