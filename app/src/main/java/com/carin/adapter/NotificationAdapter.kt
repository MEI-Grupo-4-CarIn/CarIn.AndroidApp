package com.carin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.carin.R
import com.carin.activities.NotificationActivity

class NotificationAdapter(
    private val notifications: List<NotificationActivity.Notification>
) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    private var listener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notifications, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notifications[position]
        holder.informationImageView.setImageResource(notification.imageResource)
        holder.informationTextView.text = "${notification.notificationType}"
        holder.rectangle_text.text = "${notification.temp}"
        holder.descriptionTextView.text = "${notification.description}"
        holder.countryTextView.text = "${notification.country}"

        holder.itemView.setOnClickListener {
            listener?.onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return notifications.size
    }

    class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val informationImageView: ImageView = itemView.findViewById(R.id.informationImageView)
        val informationTextView: TextView = itemView.findViewById(R.id.informationTextView)
        val rectangle_text: TextView = itemView.findViewById(R.id.rectangle_text)
        val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        val countryTextView: TextView = itemView.findViewById(R.id.countryTextView)
    }

}
