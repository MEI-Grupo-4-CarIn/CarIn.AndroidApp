package com.carin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.carin.R
import com.carin.activities.InfoVehicleActivity

class RouteInfoAdapter(
    private val routes: List<InfoVehicleActivity.RouteInfo>
) :
    RecyclerView.Adapter<RouteInfoAdapter.RouteInfoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteInfoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_routes, parent, false)
        return RouteInfoViewHolder(view)
    }

    override fun onBindViewHolder(holder: RouteInfoViewHolder, position: Int) {
        val route = routes[position]
        holder.originTextView.text = "${route.origin}"
        holder.destinationTextView.text = "${route.destination}"
        holder.dateEndTextView.text = "${route.date}"
        holder.brandTextView.text = "${route.brand}"
        holder.hourTextView.text = "${route.hour}"
        holder.kmTextView.text = "${route.km}"
    }

    override fun getItemCount(): Int {
        return routes.size
    }

    class RouteInfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val originTextView: TextView = itemView.findViewById(R.id.originTextView)
        val destinationTextView: TextView = itemView.findViewById(R.id.destinationTextView)
        val dateEndTextView: TextView = itemView.findViewById(R.id.dateEndTextView)
        val brandTextView: TextView = itemView.findViewById(R.id.brandTextView)
        val hourTextView: TextView = itemView.findViewById(R.id.hourTextView)
        val kmTextView: TextView = itemView.findViewById(R.id.kmTextView)


    }

}
