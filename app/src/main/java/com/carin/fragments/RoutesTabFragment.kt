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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.carin.R
import com.carin.adapter.RoutesTabAdapter
import com.carin.domain.enums.RouteType
import com.carin.utils.ItemSpacingDecoration
import com.carin.viewmodels.RoutesListViewModel
import com.carin.viewmodels.events.RoutesListEvent
import com.carin.viewmodels.states.RoutesListState

class RoutesTabFragment : Fragment() {

    private lateinit var adapter: RoutesTabAdapter
    private lateinit var viewModel: RoutesListViewModel
    private lateinit var currentRouteType: RouteType
    private lateinit var emptyTextView: TextView
    private lateinit var errorTextView: TextView
    private lateinit var progressBar: ProgressBar
    private var dataLoaded = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.route_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.addItemDecoration(ItemSpacingDecoration(5))
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = RoutesTabAdapter(requireContext(), mutableListOf())
        recyclerView.adapter = adapter

        emptyTextView = view.findViewById(R.id.emptyTextView)
        errorTextView = view.findViewById(R.id.errorTextView)
        progressBar = view.findViewById(R.id.progressBar)

        // Obtain the ViewModel from the Activity's ViewModelProvider
        viewModel = ViewModelProvider(requireActivity())[RoutesListViewModel::class.java]

        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is RoutesListState.Loading -> {
                    if (state.routeType == currentRouteType) {
                        progressBar.visibility = View.VISIBLE
                        errorTextView.visibility = View.GONE
                        emptyTextView.visibility = View.GONE
                    }
                }
                is RoutesListState.Success -> {
                    if (state.routeType == currentRouteType) {
                        errorTextView.visibility = View.GONE

                        if (state.isEmpty && !state.isAppending) {
                            emptyTextView.visibility = View.VISIBLE
                            recyclerView.visibility = View.GONE
                        } else {
                            emptyTextView.visibility = View.GONE
                            recyclerView.visibility = View.VISIBLE

                            if (state.isAppending)
                                adapter.appendRoutes(state.routes)
                            else
                                adapter.updateRoutes(state.routes)
                        }

                        progressBar.visibility = View.GONE
                        dataLoaded = true
                    }
                }
                is RoutesListState.Error -> {
                    if (state.routeType == currentRouteType) {
                        emptyTextView.visibility = View.GONE
                        errorTextView.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE
                    }
                }
            }
        }

        viewModel.searchQuery.observe(viewLifecycleOwner) {
            // Reload data based on the new search query
            viewModel.onEvent(RoutesListEvent.LoadRoutes(currentRouteType))
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            private val handler = Handler(Looper.getMainLooper())
            private var runnable: Runnable? = null

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                runnable?.let { handler.removeCallbacks(it) }
                runnable = Runnable {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val totalItemCount = layoutManager.itemCount
                    val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                    if (!recyclerView.canScrollVertically(1)
                        && lastVisibleItemPosition >= totalItemCount - 1) {
                        viewModel.onEvent(RoutesListEvent.LoadMoreRoutes(currentRouteType))
                    }
                }
                handler.postDelayed(runnable!!, 300)
            }
        })

        currentRouteType = (arguments?.getSerializable("routeType") as? RouteType)!!
    }

    override fun onResume() {
        super.onResume()

        viewModel.onEvent(RoutesListEvent.LoadRoutes(currentRouteType))
    }
}