package com.carin.adapter

import android.content.Intent
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import com.carin.R
import com.carin.activities.InfoVehicleActivity
import com.carin.domain.models.UserModel
import com.carin.domain.models.VehicleModel
import com.carin.fragments.VehiclesTabFragment
import java.io.ByteArrayOutputStream

class VehiclesTabAdapter(private val vehicles: MutableList<VehicleModel>) : RecyclerView.Adapter<VehiclesTabAdapter.VehicleViewHolder>() {

    private val vehiclesIds = mutableSetOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vehicle, parent, false)
        return VehicleViewHolder(view)
    }

    override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) {
        val vehicle = vehicles[position]
        holder.carImageView.setImageResource(vehicle.imageResource)
        holder.brandTextView.text = vehicle.brand
        holder.licensePlateTextView.text = vehicle.licensePlate
        holder.fuelTextView.text = vehicle.fuelType.description
        holder.consumptionTextView.text = "${vehicle.averageFuelConsumption}"
        holder.autonomyTextView.text = "${vehicle.kms}"
    }

    override fun getItemCount(): Int = vehicles.size

    fun updateVehicles(newVehicles: List<VehicleModel>) {
        vehicles.clear()
        vehiclesIds.clear()
        addVehiclesToAdapter(newVehicles)
    }

    fun appendVehicles(newVehicles: List<VehicleModel>) {
        addVehiclesToAdapter(newVehicles)
    }

    private fun addVehiclesToAdapter(newVehicles: List<VehicleModel>) {
        val filteredVehicles = newVehicles.filter { vehiclesIds.add(it.id) }
        vehicles.addAll(filteredVehicles)
        notifyDataSetChanged()
    }

    class VehicleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val carImageView: ImageView = itemView.findViewById(R.id.carImageView)
        val brandTextView: TextView = itemView.findViewById(R.id.brandTextView)
        val licensePlateTextView: TextView = itemView.findViewById(R.id.licensePlateTextView)
        val fuelTextView: TextView = itemView.findViewById(R.id.fuelTextView)
        val consumptionTextView: TextView = itemView.findViewById(R.id.consumptionTextView)
        val autonomyTextView: TextView = itemView.findViewById(R.id.autonomyTextView)
        val backgroundRectangleImageView: ImageView = itemView.findViewById(R.id.backgroundRectangle)

        init {
            backgroundRectangleImageView.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, InfoVehicleActivity::class.java)
                val carBitmap: Bitmap = carImageView.drawable.toBitmap()
                val byteArrayOutputStream = ByteArrayOutputStream()
                carBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
                intent.putExtra("car_image", byteArray)
                intent.putExtra("brand_text", brandTextView.text.toString())
                context.startActivity(intent)
            }
        }

    }

}
