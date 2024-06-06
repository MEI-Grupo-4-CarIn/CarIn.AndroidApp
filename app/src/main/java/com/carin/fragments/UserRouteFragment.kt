package com.carin.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.carin.R
import com.carin.activities.InfoRouteActivity
import com.carin.activities.InfoVehicleActivity
import com.carin.adapter.RouteInfoAdapter
import com.carin.utils.ItemSpacingDecoration

class UserRouteFragment : Fragment() {

    private lateinit var adapter: RouteInfoAdapter
    private lateinit var routes: List<InfoVehicleActivity.RouteInfo>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.user_route_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)

        recyclerView.addItemDecoration(ItemSpacingDecoration(5))

        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        routes = getRoute()

        adapter = RouteInfoAdapter(routes)
        recyclerView.adapter = adapter

        adapter.setOnItemClickListener(object : RouteInfoAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val intent = Intent(requireContext(), InfoRouteActivity::class.java)
                startActivity(intent)
            }
        })

    }


    private fun getRoute(): MutableList<InfoVehicleActivity.RouteInfo> {
        val routes = mutableListOf<InfoVehicleActivity.RouteInfo>()
        routes.add(
            InfoVehicleActivity.RouteInfo(
                "Porto",
                "Luxemburgo",
                "12 Fevereiro",
                "INFINITY Vision Qe",
                "18H30m",
                "1700"
            )
        )
        routes.add(
            InfoVehicleActivity.RouteInfo(
                "Lisboa",
                "Luxemburgo",
                "13 Fevereiro",
                "INFINITY Vision Qe",
                "18H30m",
                "1700"
            )
        )
        routes.add(
            InfoVehicleActivity.RouteInfo(
                "Porto",
                "Luxemburgo",
                "12 Fevereiro",
                "INFINITY Vision Qe",
                "18H30m",
                "1700"
            )
        )
        routes.add(
            InfoVehicleActivity.RouteInfo(
                "Lisboa",
                "Luxemburgo",
                "13 Fevereiro",
                "INFINITY Vision Qe",
                "18H30m",
                "1700"
            )
        )
        routes.add(
            InfoVehicleActivity.RouteInfo(
                "Porto",
                "Luxemburgo",
                "12 Fevereiro",
                "INFINITY Vision Qe",
                "18H30m",
                "1700"
            )
        )
        routes.add(
            InfoVehicleActivity.RouteInfo(
                "Lisboa",
                "Luxemburgo",
                "13 Fevereiro",
                "INFINITY Vision Qe",
                "18H30m",
                "1700"
            )
        )
        return routes
    }
}