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
import com.carin.fragments.RoutesTabFragment

class RoutesTabAdapter(private val routes: List<RoutesTabFragment.Route>) : RecyclerView.Adapter<RoutesTabAdapter.RouteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_route, parent, false)
        return RouteViewHolder(view)
    }

    override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {
        val route = routes[position]
        holder.originTextView.text = "${route.origin}"
        holder.destinationTextView.text = "${route.destination}"
        holder.driverNameTextView.text = "${route.driver}"
        holder.hoursTextView.text = "${route.hours}"
        holder.departureDateTextView.text = "${route.departureDate}"
        holder.vehicleTextView.text = "${route.vehicle}"
        holder.kmTextView.text = "${route.km}"
    }

    override fun getItemCount(): Int {
        return routes.size
    }

    class RouteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val originTextView: TextView = itemView.findViewById(R.id.originTextView)
        val destinationTextView: TextView = itemView.findViewById(R.id.destinationTextView)
        val driverNameTextView: TextView = itemView.findViewById(R.id.driverNameTextView)
        val hoursTextView: TextView = itemView.findViewById(R.id.hoursTextView)
        val departureDateTextView: TextView = itemView.findViewById(R.id.departureDateTextView)
        val vehicleTextView: TextView = itemView.findViewById(R.id.vehicleTextView)
        val kmTextView: TextView = itemView.findViewById(R.id.kmTextView)
        val backgroundRectangleImageView: FrameLayout = itemView.findViewById(R.id.backgroundRectangle)

        init {
            backgroundRectangleImageView.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, InfoRouteActivity::class.java)
                context.startActivity(intent)
            }
        }

    }

}