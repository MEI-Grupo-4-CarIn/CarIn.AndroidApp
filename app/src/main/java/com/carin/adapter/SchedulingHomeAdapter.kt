package com.carin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.carin.R
import com.carin.activities.HomeActivity

class SchedulingHomeAdapter(
    private val schedulings: List<HomeActivity.Schedulings>
) :
    RecyclerView.Adapter<SchedulingHomeAdapter.SchedulingsViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    private var listener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SchedulingsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_scheduling, parent, false)
        return SchedulingsViewHolder(view)
    }

    override fun onBindViewHolder(holder: SchedulingsViewHolder, position: Int) {
        val scheduling = schedulings[position]
        holder.textViewTime.text = "${scheduling.hour}"
        holder.text1ViewText.text = "${scheduling.inf}"
        holder.otherTextView.text = "${scheduling.otherHour}"

        holder.itemView.setOnClickListener {
            listener?.onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return schedulings.size
    }

    class SchedulingsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewTime: TextView = itemView.findViewById(R.id.textViewTime)
        val text1ViewText: TextView = itemView.findViewById(R.id.text1ViewText)
        val otherTextView: TextView = itemView.findViewById(R.id.otherTextView)
    }
}
