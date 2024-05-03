package com.carin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.carin.R
import com.carin.activities.HomeActivity

class HomeNotificationAdapter(
    private val notifications: List<HomeActivity.Notification>
) :
    RecyclerView.Adapter<HomeNotificationAdapter.NotificationViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    private var listener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home_notifications, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notifications[position]
        holder.image.setImageResource(notification.imageResource)
        holder.text1.text = "${notification.notificationType}"
        holder.text_icon.text = "${notification.country}"
        holder.rectangle_text.text = "${notification.temp}"

        holder.itemView.setOnClickListener {
            listener?.onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return notifications.size
    }

    class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.image)
        val text1: TextView = itemView.findViewById(R.id.text1)
        val text_icon: TextView = itemView.findViewById(R.id.text_icon)
        val rectangle_text: TextView = itemView.findViewById(R.id.rectangle_text)
    }

}
