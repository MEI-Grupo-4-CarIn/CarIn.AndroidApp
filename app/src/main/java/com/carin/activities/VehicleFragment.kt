package com.carin.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.carin.R
import com.carin.adapter.VehicleAdapter

class VehicleFragment : Fragment() {

    private lateinit var adapter: VehicleAdapter
    private lateinit var vehicles: List<VehicleFragment.Vehicle>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.vehicle_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)

        recyclerView.addItemDecoration(ItemSpacingDecoration(5))

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        vehicles = getVehicles()

        adapter = VehicleAdapter(vehicles)
        recyclerView.adapter = adapter
    }


    private fun getVehicles(): List<Vehicle> {

        val vehicles = mutableListOf<Vehicle>()
        vehicles.add(Vehicle(R.drawable.ic_vehicle_blue, "INFINITY Vision Qe","AB-89-UD", "Elétrico", "5.7 L/100km", "150kms"))
        vehicles.add(Vehicle(R.drawable.ic_vehicle_blue, "INFINITY Vision Qe","AB-89-UD", "Elétrico", "5.7 L/100km", "150kms"))
        vehicles.add(Vehicle(R.drawable.ic_vehicle_blue, "INFINITY Vision Qe","AB-89-UD", "Elétrico", "5.7 L/100km", "150kms"))
        vehicles.add(Vehicle(R.drawable.ic_vehicle_blue, "INFINITY Vision Qe","AB-89-UD", "Elétrico", "5.7 L/100km", "150kms"))
        vehicles.add(Vehicle(R.drawable.ic_vehicle_blue, "INFINITY Vision Qe","AB-89-UD", "Elétrico", "5.7 L/100km", "150kms"))
        vehicles.add(Vehicle(R.drawable.ic_vehicle_blue, "INFINITY Vision Qe","AB-89-UD", "Elétrico", "5.7 L/100km", "150kms"))
        vehicles.add(Vehicle(R.drawable.ic_vehicle_blue, "INFINITY Vision Qe","AB-89-UD", "Elétrico", "5.7 L/100km", "150kms"))
        vehicles.add(Vehicle(R.drawable.ic_vehicle_blue, "INFINITY Vision Qe","AB-89-UD", "Elétrico", "5.7 L/100km", "150kms"))

        return vehicles
    }
    data class Vehicle(val imageResource: Int, val brand: String,  val licenseplate: String, val fuel: String, val consumption: String, val autonomy: String)
}