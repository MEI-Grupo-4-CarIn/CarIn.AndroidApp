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
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.random.Random

class UsersHomeAdapter(
    private val users: MutableList<UserModel>) :
    RecyclerView.Adapter<UsersHomeAdapter.UserHomeViewHolder>() {

    private val userIds = mutableSetOf<Int>()
    private val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersHomeAdapter.UserHomeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home_employees, parent, false)
        return UserHomeViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserHomeViewHolder, position: Int) {
        val user = users[position]
        holder.image.setImageResource(user.imageResource)
        holder.nameText.text = "${user.firstName} ${user.lastName}"
        holder.role.text = user.role.toString()
        holder.email.text = user.email
        holder.birthday.text = " ${formatter.format(user.birthDate)}"
        holder.journey.text = generateRandomJourney()
        holder.hours.text = generateRandomHours()
        holder.kms.text = generateRandomKms()
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

    inner class UserHomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.image)
        val nameText: TextView = itemView.findViewById(R.id.text1)
        val role: TextView = itemView.findViewById(R.id.text2)
        val email: TextView = itemView.findViewById(R.id.email)
        val birthday: TextView = itemView.findViewById(R.id.birthday)
        val journey: TextView = itemView.findViewById(R.id.journeys)
        val hours: TextView = itemView.findViewById(R.id.hours)
        val kms: TextView = itemView.findViewById(R.id.kms)
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

    private fun generateRandomJourney(): String {
        return "${Random.nextInt(100, 1000)}"
    }

    private fun generateRandomHours(): String {
        val days = Random.nextInt(0, 30)
        val hours = Random.nextInt(0, 24)
        val minutes = Random.nextInt(0, 60)
        return "${days}d${hours}h${minutes}m"
    }

    private fun generateRandomKms(): String {
        return "${Random.nextInt(1000, 50000)} km"
    }
}
