package com.carin.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.carin.domain.enums.UserType
import com.carin.viewmodels.UsersListViewModel
import com.carin.viewmodels.events.UsersListEvent
import com.carin.viewmodels.states.UsersListState

class UsersTabFragment : Fragment() {

    private lateinit var adapter: UsersTabAdapter
    private lateinit var viewModel: UsersListViewModel
    private lateinit var currentUserType: UserType
    private lateinit var emptyTextView: TextView
    private lateinit var errorTextView: TextView
    private lateinit var progressBar: ProgressBar
    private var dataLoaded = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
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
        viewModel = ViewModelProvider(requireActivity())[UsersListViewModel::class.java]

        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UsersListState.Loading -> {
                    if (state.userType == currentUserType) {
                        progressBar.visibility = View.VISIBLE
                        errorTextView.visibility = View.GONE
                        emptyTextView.visibility = View.GONE
                    }
                }
                is UsersListState.Success -> {
                    if (state.userType == currentUserType) {
                        errorTextView.visibility = View.GONE

                        if (state.isEmpty && !state.isAppending) {
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

                        progressBar.visibility = View.GONE
                        dataLoaded = true
                    }
                }
                is UsersListState.Error -> {
                    if (state.userType == currentUserType) {
                        emptyTextView.visibility = View.GONE
                        errorTextView.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE
                    }
                }
            }
        }

        viewModel.searchQuery.observe(viewLifecycleOwner) {
            // Reload data based on the new search query
            viewModel.onEvent(UsersListEvent.LoadUsers(currentUserType))
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            private val handler = Handler(Looper.getMainLooper())
            private var runnable: Runnable? = null

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                runnable?.let { handler.removeCallbacks(it) }
                runnable = Runnable {
                    val layoutManager = recyclerView.layoutManager as GridLayoutManager
                    val totalItemCount = layoutManager.itemCount
                    val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                    if (!recyclerView.canScrollVertically(1)
                        && lastVisibleItemPosition >= totalItemCount - 2) {
                        viewModel.onEvent(UsersListEvent.LoadMoreUsers(currentUserType))
                    }
                }
                handler.postDelayed(runnable!!, 300)
            }
        })

        currentUserType = (arguments?.getSerializable("userType") as? UserType)!!
    }

    override fun onResume() {
        super.onResume()
        if (!dataLoaded)
            viewModel.onEvent(UsersListEvent.LoadUsers(currentUserType))
    }
}
