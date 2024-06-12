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
import com.carin.adapter.VehiclesTabAdapter
import com.carin.domain.enums.VehicleType
import com.carin.viewmodels.VehiclesListViewModel
import com.carin.viewmodels.events.VehiclesListEvent
import com.carin.viewmodels.states.VehiclesListState

class VehiclesTabFragment : Fragment() {

    private lateinit var adapter: VehiclesTabAdapter
    private lateinit var viewModel: VehiclesListViewModel
    private lateinit var currentVehicleType: VehicleType
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
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = VehiclesTabAdapter(requireContext(), mutableListOf())
        recyclerView.adapter = adapter

        emptyTextView = view.findViewById(R.id.emptyTextView)
        errorTextView = view.findViewById(R.id.errorTextView)
        progressBar = view.findViewById(R.id.progressBar)

        // Obtain the ViewModel from the Activity's ViewModelProvider
        viewModel = ViewModelProvider(requireActivity())[VehiclesListViewModel::class.java]

        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is VehiclesListState.Loading -> {
                    if (state.vehicleType == currentVehicleType) {
                        progressBar.visibility = View.VISIBLE
                        errorTextView.visibility = View.GONE
                        emptyTextView.visibility = View.GONE
                    }
                }
                is VehiclesListState.Success -> {
                    if (state.vehicleType == currentVehicleType) {
                        errorTextView.visibility = View.GONE

                        if (state.isEmpty && !state.isAppending) {
                            emptyTextView.visibility = View.VISIBLE
                            recyclerView.visibility = View.GONE
                        } else {
                            emptyTextView.visibility = View.GONE
                            recyclerView.visibility = View.VISIBLE

                            if (state.isAppending)
                                adapter.appendVehicles(state.vehicles)
                            else
                                adapter.updateVehicles(state.vehicles)
                        }

                        progressBar.visibility = View.GONE
                        dataLoaded = true
                    }
                }
                is VehiclesListState.Error -> {
                    if (state.vehicleType == currentVehicleType) {
                        emptyTextView.visibility = View.GONE
                        errorTextView.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE
                    }
                }
            }
        }

        viewModel.searchQuery.observe(viewLifecycleOwner) {
            // Reload data based on the new search query
            viewModel.onEvent(VehiclesListEvent.LoadVehicles(currentVehicleType))
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
                        viewModel.onEvent(VehiclesListEvent.LoadMoreVehicles(currentVehicleType))
                    }
                }
                handler.postDelayed(runnable!!, 300)
            }
        })

        currentVehicleType = (arguments?.getSerializable("vehicleType") as? VehicleType)!!
    }

    override fun onResume() {
        super.onResume()
            viewModel.onEvent(VehiclesListEvent.LoadVehicles(currentVehicleType))
    }
}
