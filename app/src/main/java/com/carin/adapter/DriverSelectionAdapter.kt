package com.carin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.carin.R
import com.carin.domain.models.UserModel

class DriverSelectionAdapter(private var drivers: List<UserModel>, private val onUserSelected: (UserModel) -> Unit) : RecyclerView.Adapter<DriverSelectionAdapter.DriverViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DriverViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_driver_selection, parent, false)
        return DriverViewHolder(view)
    }

    override fun onBindViewHolder(holder: DriverViewHolder, position: Int) {
        holder.bind(drivers[position])
    }

    override fun getItemCount() = drivers.size

    fun updateDrivers(newUsers: List<UserModel>) {
        drivers = newUsers
        notifyDataSetChanged()
    }

    inner class DriverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameViewTime: TextView = itemView.findViewById(R.id.nameViewTime)
        private val emailTextView: TextView = itemView.findViewById(R.id.emailTextView)

        fun bind(user: UserModel) {
            nameViewTime.text = "${user.firstName} ${user.lastName}"
            emailTextView.text = user.email

            itemView.setOnClickListener {
                onUserSelected(user)
            }
        }
    }
}