package com.carin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.carin.R
import com.carin.activities.InfoVehicleActivity

class UserAdapter(
    private val users: List<InfoVehicleActivity.User>) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_drivers, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.circularImageView.setImageResource(user.image)
        holder.firstNameViewTime.text = "${user.firstName}"
        holder.lastNameViewTime.text = "${user.lastName}"

    }

    override fun getItemCount(): Int {
        return users.size
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val circularImageView: ImageView = itemView.findViewById(R.id.circularImageView)
        val firstNameViewTime: TextView = itemView.findViewById(R.id.firstNameViewTime)
        val lastNameViewTime: TextView = itemView.findViewById(R.id.lastNameViewTime)
    }
}

