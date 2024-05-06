package com.carin.adapter

import android.content.Intent
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import com.carin.R
import com.carin.activities.InfoUserActivity
import com.carin.activities.UserFragment
import java.io.ByteArrayOutputStream

class EmployeesAdapter(
    private val employees: List<UserFragment.Employee>) :
    RecyclerView.Adapter<EmployeesAdapter.EmployeeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_users, parent, false)
        return EmployeeViewHolder(view)
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        val employee = employees[position]
        holder.imageView.setImageResource(employee.imageResource)
        holder.textView1.text = "${employee.name}"
        holder.textView2.text = "${employee.email}"
    }

    override fun getItemCount(): Int {
        return employees.size
    }

    class EmployeeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val textView1: TextView = itemView.findViewById(R.id.textView1)
        val textView2: TextView = itemView.findViewById(R.id.textView2)
        val backgroundRectangleImageView: ImageView = itemView.findViewById(R.id.backgroundRectangle)
        init {
            backgroundRectangleImageView.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, InfoUserActivity::class.java)
                val personBitmap: Bitmap = imageView.drawable.toBitmap()
                val byteArrayOutputStream = ByteArrayOutputStream()
                personBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
                intent.putExtra("imageResource", byteArray)
                intent.putExtra("name", textView1.text.toString())
                intent.putExtra("email", textView2.text.toString())
                context.startActivity(intent)
            }
        }
    }
}
