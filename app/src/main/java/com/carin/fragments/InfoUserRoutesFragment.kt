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
import com.carin.adapter.RouteInfoAdapter
import com.carin.utils.AuthUtils
import com.carin.viewmodels.InfoUserViewModel
import com.carin.viewmodels.states.RoutesListState

class InfoUserRoutesFragment : Fragment() {

    private lateinit var adapter: RouteInfoAdapter
    private lateinit var viewModel: InfoUserViewModel
    private lateinit var emptyTextView: TextView
    private lateinit var errorTextView: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.info_user_routes_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        adapter = RouteInfoAdapter(mutableListOf())
        recyclerView.adapter = adapter

        emptyTextView = view.findViewById(R.id.emptyTextView)
        errorTextView = view.findViewById(R.id.errorTextView)
        progressBar = view.findViewById(R.id.progressBar)

        // Obtain the ViewModel from the Activity's ViewModelProvider
        viewModel = ViewModelProvider(requireActivity())[InfoUserViewModel::class.java]
        viewModel.uiRoutesState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is RoutesListState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    errorTextView.visibility = View.GONE
                    emptyTextView.visibility = View.GONE
                }
                is RoutesListState.Success -> {
                    errorTextView.visibility = View.GONE

                    if (state.isEmpty) {
                        emptyTextView.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                    } else {
                        emptyTextView.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                        adapter.updateRoutes(state.routes)
                    }

                    progressBar.visibility = View.GONE
                }
                is RoutesListState.Error -> {
                    emptyTextView.visibility = View.GONE
                    errorTextView.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                }
            }
        }

        // Load user routes list
        val userIdFromList = requireActivity().intent.getIntExtra("id", -1)
        if (userIdFromList == -1) {
            val userAuth = AuthUtils.getUserAuth(requireContext())
            if (userAuth != null) {
                viewModel.loadRoutesForUser(userAuth.userId)
            }
        } else {
            viewModel.loadRoutesForUser(userIdFromList)
        }
    }
}