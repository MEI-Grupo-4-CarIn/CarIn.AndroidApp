package com.carin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.carin.R
import com.carin.activities.HomeActivity

class EmployeesHomeAdapter(
    private val employees: List<HomeActivity.Employee>) :
    RecyclerView.Adapter<EmployeesHomeAdapter.EmployeeHomeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeHomeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home_employees, parent, false)
        return EmployeeHomeViewHolder(view)
    }

    override fun onBindViewHolder(holder: EmployeeHomeViewHolder, position: Int) {
        val employee = employees[position]
        holder.image.setImageResource(employee.image)
        holder.text1.text = "${employee.name}"
        holder.text2.text = "${employee.function}"
        holder.email.text = "${employee.email}"
        holder.birthday.text = "${employee.birthday}"
        holder.journeys.text = "${employee.journeys}"
        holder.hours.text = "${employee.hours}"
        holder.kms.text = "${employee.kms}"
    }

    override fun getItemCount(): Int {
        return employees.size
    }

    class EmployeeHomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.image)
        val text1: TextView = itemView.findViewById(R.id.text1)
        val text2: TextView = itemView.findViewById(R.id.text2)
        val email: TextView = itemView.findViewById(R.id.email)
        val birthday: TextView = itemView.findViewById(R.id.birthday)
        val journeys: TextView = itemView.findViewById(R.id.journeys)
        val hours: TextView = itemView.findViewById(R.id.hours)
        val kms: TextView = itemView.findViewById(R.id.kms)
    }
}
