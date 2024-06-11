package com.carin.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.carin.R
import com.carin.activities.InfoRouteActivity
import com.carin.domain.models.RouteModel
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale

class RouteInfoAdapter(private val routes: MutableList<RouteModel>) : RecyclerView.Adapter<RouteInfoAdapter.RouteInfoViewHolder>() {

    private val routeIds = mutableSetOf<String>()
    private val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    private val decimalFormat = DecimalFormat("#.##")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteInfoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_routes, parent, false)
        return RouteInfoViewHolder(view)
    }

    override fun onBindViewHolder(holder: RouteInfoViewHolder, position: Int) {
        val route = routes[position]
        holder.originTextView.text = route.startPoint.city
        holder.destinationTextView.text = route.endPoint.city
        holder.departureDateTextView.text = " ${formatter.format(route.startDate)}"
        holder.vehicleTextView.text = "${route.vehicle?.brand} ${route.vehicle?.model}"
        holder.hourTextView.text = "${route.duration}"
        holder.kmTextView.text = "${decimalFormat.format(route.distance)} km"
    }

    override fun getItemCount(): Int = routes.size

    fun updateRoutes(newRoutes: List<RouteModel>) {
        routes.clear()
        routeIds.clear()
        addRoutesToAdapter(newRoutes)
    }

    private fun addRoutesToAdapter(newRoutes: List<RouteModel>) {
        val filteredUsers = newRoutes.filter { routeIds.add(it.id) }
        routes.addAll(filteredUsers)
        notifyDataSetChanged()
    }

    inner class RouteInfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val originTextView: TextView = itemView.findViewById(R.id.originTextView)
        val destinationTextView: TextView = itemView.findViewById(R.id.destinationTextView)
        val departureDateTextView: TextView = itemView.findViewById(R.id.departureDateTextView)
        val vehicleTextView: TextView = itemView.findViewById(R.id.vehicleTextView)
        val hourTextView: TextView = itemView.findViewById(R.id.hourTextView)
        val kmTextView: TextView = itemView.findViewById(R.id.kmTextView)
        val backgroundRectangleImageView: FrameLayout = itemView.findViewById(R.id.backgroundRectangle)

        init {
            backgroundRectangleImageView.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, InfoRouteActivity::class.java)
                intent.putExtra("routeId", routes[adapterPosition].id)
                context.startActivity(intent)
            }
        }
    }

}
