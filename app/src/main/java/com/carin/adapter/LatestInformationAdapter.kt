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
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_SHORT = 0
        private const val TYPE_LONG = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (latestInformations[position].information.length > 50) {
            TYPE_LONG
        } else {
            TYPE_SHORT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_LONG -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_latest_information_red, parent, false)
                LongInformationViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_latest_information, parent, false)
                LatestInformationViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val latestInformation = latestInformations[position]
        when (holder) {
            is LatestInformationViewHolder -> {
                holder.icon.setImageResource(latestInformation.icon)
                holder.textView1.text = latestInformation.typeInformation
                holder.textView2.text = latestInformation.information
            }
            is LongInformationViewHolder -> {
                holder.icon.setImageResource(latestInformation.icon)
                holder.textView1.text = latestInformation.typeInformation
                holder.textView2.text = latestInformation.information
            }
        }
    }

    override fun getItemCount(): Int {
        return latestInformations.size
    }

    class LatestInformationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.icon)
        val textView1: TextView = itemView.findViewById(R.id.textView1)
        val textView2: TextView = itemView.findViewById(R.id.textView2)
    }

    class LongInformationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.icon)
        val textView1: TextView = itemView.findViewById(R.id.textView1)
        val textView2: TextView = itemView.findViewById(R.id.textView2)
    }
}
