package com.carin.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.carin.R
import com.carin.activities.InfoUserActivity
import com.carin.domain.models.UserModel

class UserInfoAdapter(private val users: MutableList<UserModel>) : RecyclerView.Adapter<UserInfoAdapter.UserInfoViewHolder>() {

    private val userIds = mutableSetOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserInfoAdapter.UserInfoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_drivers, parent, false)
        return UserInfoViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserInfoViewHolder, position: Int) {
        val user = users[position]
        holder.circularImageView.setImageResource(user.imageResource)
        holder.nameText.text = "${user.firstName} ${user.lastName}"
        holder.itemView.tag = user.id
    }

    override fun getItemCount(): Int = users.size

    fun updateUsers(newUsers: List<UserModel>) {
        users.clear()
        userIds.clear()
        addUsersToAdapter(newUsers)
    }

    private fun addUsersToAdapter(newUsers: List<UserModel>) {
        val filteredUsers = newUsers.filter { userIds.add(it.id) }
        users.addAll(filteredUsers)
        notifyDataSetChanged()
    }

    inner class UserInfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val circularImageView: ImageView = itemView.findViewById(R.id.circularImageView)
        val nameText: TextView = itemView.findViewById(R.id.nameTextView)

        val backgroundRectangleImageView: FrameLayout = itemView.findViewById(R.id.backgroundRectangle)

        init {
            backgroundRectangleImageView.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, InfoUserActivity::class.java)
                val userId = itemView.tag as Int

                intent.putExtra("id", userId)
                intent.putExtra("name", nameText.text.toString())
                context.startActivity(intent)
            }
        }
    }
}

