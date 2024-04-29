package com.carin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.carin.R
import com.carin.activities.HomeActivity

class LatestInformationAdapter(
    private val latestInformations: List<HomeActivity.LatestInformation>
) :
    RecyclerView.Adapter<LatestInformationAdapter.LatestInformationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LatestInformationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_latest_information, parent, false)
        return LatestInformationViewHolder(view)
    }

    override fun onBindViewHolder(holder: LatestInformationViewHolder, position: Int) {
        val latestInformation = latestInformations[position]
        holder.icon.setImageResource(latestInformation.icon)
        holder.textView1.text = "${latestInformation.typeInformation}"
        holder.textView2.text = "${latestInformation.information}"
    }

    override fun getItemCount(): Int {
        return latestInformations.size
    }

    class LatestInformationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.icon)
        val textView1: TextView = itemView.findViewById(R.id.textView1)
        val textView2: TextView = itemView.findViewById(R.id.textView2)
    }

}
