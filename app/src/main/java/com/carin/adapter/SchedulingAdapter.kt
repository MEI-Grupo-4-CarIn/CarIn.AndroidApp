package com.carin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.carin.R
import com.carin.activities.InfoVehicleActivity

class SchedulingAdapter(
    private val schedulings: List<InfoVehicleActivity.Scheduling>
) :
    RecyclerView.Adapter<SchedulingAdapter.SchedulingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SchedulingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_scheduling, parent, false)
        return SchedulingViewHolder(view)
    }

    override fun onBindViewHolder(holder: SchedulingViewHolder, position: Int) {
        val scheduling = schedulings[position]
        holder.textViewTime.text = "${scheduling.hour}"
        holder.text1ViewText.text = "${scheduling.inf}"
        holder.otherTextView.text = "${scheduling.otherHour}"

    }

    override fun getItemCount(): Int {
        return schedulings.size
    }

    class SchedulingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewTime: TextView = itemView.findViewById(R.id.textViewTime)
        val text1ViewText: TextView = itemView.findViewById(R.id.text1ViewText)
        val otherTextView: TextView = itemView.findViewById(R.id.otherTextView)

    }
}

