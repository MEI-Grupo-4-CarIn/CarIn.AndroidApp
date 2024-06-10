package com.carin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.carin.R
import com.carin.domain.models.VehicleModel
import com.carin.utils.getStringResourceByName

class VehicleSelectionAdapter(
    private val context: Context,
    private var vehicles: List<VehicleModel>,
    private val onVehicleSelected: (VehicleModel) -> Unit
) : RecyclerView.Adapter<VehicleSelectionAdapter.VehicleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_vehicle_selection, parent, false)
        return VehicleViewHolder(view)
    }

    override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) {
        holder.bind(vehicles[position])
    }

    override fun getItemCount() = vehicles.size

    fun updateVehicles(newVehicles: List<VehicleModel>) {
        vehicles = newVehicles
        notifyDataSetChanged()
    }

    inner class VehicleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val brandTextView: TextView = itemView.findViewById(R.id.brandTextView)
        private val fuelTextView: TextView = itemView.findViewById(R.id.fuelTextView)
        private val consumptionTextView: TextView = itemView.findViewById(R.id.consumptionTextView)
        private val autonomyTextView: TextView = itemView.findViewById(R.id.autonomyTextView)

        fun bind(vehicle: VehicleModel) {
            brandTextView.text = "${vehicle.brand} ${vehicle.model}"
            fuelTextView.text = context.getStringResourceByName(vehicle.fuelType.stringKey)
            consumptionTextView.text = "${vehicle.averageFuelConsumption} l/100km"
            autonomyTextView.text = "${vehicle.kms.toInt()} km"

            itemView.setOnClickListener {
                onVehicleSelected(vehicle)
            }
        }
    }
}