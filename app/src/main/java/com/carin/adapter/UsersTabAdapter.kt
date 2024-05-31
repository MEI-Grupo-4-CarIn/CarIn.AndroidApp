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
import com.carin.domain.models.UserModel
import java.io.ByteArrayOutputStream

class UsersTabAdapter(private var users: MutableList<UserModel>) : RecyclerView.Adapter<UsersTabAdapter.UserViewHolder>() {

    private val userIds = mutableSetOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_users, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.imageView.setImageResource(user.imageResource)
        holder.textView1.text = "${user.firstName} ${user.lastName}"
        holder.textView2.text = "${user.email}"
    }

    override fun getItemCount(): Int = users.size

    fun updateUsers(newUsers: List<UserModel>) {
        users.clear()
        userIds.clear()
        addUsersToAdapter(newUsers)
    }

    fun appendUsers(newUsers: List<UserModel>) {
        addUsersToAdapter(newUsers)
    }

    private fun addUsersToAdapter(newUsers: List<UserModel>) {
        val filteredUsers = newUsers.filter { userIds.add(it.id) }
        users.addAll(filteredUsers)
        notifyDataSetChanged()
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val textView1: TextView = itemView.findViewById(R.id.textView1)
        val textView2: TextView = itemView.findViewById(R.id.textView2)
        private val backgroundRectangleImageView: ImageView = itemView.findViewById(R.id.backgroundRectangle)
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
