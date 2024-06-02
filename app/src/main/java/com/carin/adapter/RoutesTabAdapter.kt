package com.carin.adapter

import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.drawable.LayerDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.carin.R
import com.carin.activities.InfoRouteActivity
import com.carin.domain.enums.RouteStatus
import com.carin.domain.models.RouteModel
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale

class RoutesTabAdapter(private val routes: MutableList<RouteModel>) : RecyclerView.Adapter<RoutesTabAdapter.RouteViewHolder>() {

    private val routeIds = mutableSetOf<String>()
    private val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    private val decimalFormat = DecimalFormat("#.##")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_route, parent, false)
        return RouteViewHolder(view)
    }

    override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {
        val route = routes[position]
        holder.originTextView.text = route.startPoint.city
        holder.destinationTextView.text = route.endPoint.city
        holder.driverNameTextView.text = "${route.user?.firstName} ${route.user?.lastName}"
        holder.hoursTextView.text = "${route.duration}"
        holder.departureDateTextView.text = formatter.format(route.startDate)
        holder.statusTextView.text = route.status.description
        holder.vehicleTextView.text = "INFINITY Vision Qe"
        holder.kmTextView.text = "${decimalFormat.format(route.distance)} km"

        val (progress, color) = getProgressAndColor(route.status, holder.itemView.context)
        holder.timeProgressBar.progress = progress

        val layerDrawable = holder.timeProgressBar.progressDrawable as LayerDrawable
        val progressDrawable = layerDrawable.findDrawableByLayerId(android.R.id.progress)
        progressDrawable.setColorFilter(color, PorterDuff.Mode.SRC_IN)
    }

    override fun getItemCount(): Int = routes.size

    fun updateRoutes(newRoutes: List<RouteModel>) {
        routes.clear()
        routeIds.clear()
        addRoutesToAdapter(newRoutes)
    }

    fun appendRoutes(newRoutes: List<RouteModel>) {
        addRoutesToAdapter(newRoutes)
    }

    private fun addRoutesToAdapter(newRoutes: List<RouteModel>) {
        val filteredUsers = newRoutes.filter { routeIds.add(it.id) }
        routes.addAll(filteredUsers)
        notifyDataSetChanged()
    }

    private fun getProgressAndColor(status: RouteStatus, context: Context): Pair<Int, Int> {
        return when (status) {
            RouteStatus.Pending -> Pair(100, ContextCompat.getColor(context, R.color.colorPending))
            RouteStatus.InProgress -> Pair(50, ContextCompat.getColor(context, R.color.colorInProgress))
            RouteStatus.Cancelled -> Pair(100, ContextCompat.getColor(context, R.color.colorCancelled))
            RouteStatus.Completed -> Pair(100, ContextCompat.getColor(context, R.color.colorCompleted))
        }
    }

    class RouteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val originTextView: TextView = itemView.findViewById(R.id.originTextView)
        val destinationTextView: TextView = itemView.findViewById(R.id.destinationTextView)
        val driverNameTextView: TextView = itemView.findViewById(R.id.driverNameTextView)
        val hoursTextView: TextView = itemView.findViewById(R.id.hoursTextView)
        val departureDateTextView: TextView = itemView.findViewById(R.id.departureDateTextView)
        val statusTextView: TextView = itemView.findViewById(R.id.statusTextView)
        val vehicleTextView: TextView = itemView.findViewById(R.id.vehicleTextView)
        val kmTextView: TextView = itemView.findViewById(R.id.kmTextView)
        val timeProgressBar: ProgressBar = itemView.findViewById(R.id.timeProgressBar)
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
