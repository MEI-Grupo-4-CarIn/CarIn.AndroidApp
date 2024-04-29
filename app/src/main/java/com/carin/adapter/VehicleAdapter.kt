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
import com.carin.activities.VehicleActivity
import java.io.ByteArrayOutputStream

class VehicleAdapter(
    private val vehicles: List<VehicleActivity.Vehicle>) :
    RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vehicle, parent, false)
        return VehicleViewHolder(view)
    }

    override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) {
        val vehicle = vehicles[position]
        holder.carImageView.setImageResource(vehicle.imageResource)
        holder.brandTextView.text = "${vehicle.brand}"
        holder.licensePlateTextView.text = "${vehicle.licenseplate}"
        holder.fuelTextView.text = "${vehicle.fuel}"
        holder.consumptionTextView.text = "${vehicle.consumption}"
        holder.autonomyTextView.text = "${vehicle.autonomy}"
    }

    override fun getItemCount(): Int {
        return vehicles.size
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
