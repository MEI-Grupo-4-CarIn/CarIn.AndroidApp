package com.carin.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.carin.R
import com.carin.adapter.NotificationAdapter
import com.carin.utils.ItemSpacingDecoration

class NotificationActivity : AppCompatActivity() {

    private lateinit var adapter: NotificationAdapter
    private lateinit var notifications: List<Notification>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)

        recyclerView.addItemDecoration(ItemSpacingDecoration(10))

        recyclerView.layoutManager = LinearLayoutManager(this)

        notifications = getNotifications()

        adapter = NotificationAdapter(notifications)
        recyclerView.adapter = adapter

        adapter.setOnItemClickListener(object : NotificationAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val intent = Intent(this@NotificationActivity, InfoRouteActivity::class.java)
                startActivity(intent)
            }
        })

        val backNotification: ImageView = findViewById(R.id.iconImageView)

        backNotification.setOnClickListener {
            Intent(this, HomeActivity::class.java)
            overridePendingTransition(R.animator.slide_in_right, R.animator.slide_out_left)
            finish()
        }
    }

    private fun getNotifications(): List<Notification> {

        val notifications = mutableListOf<Notification>()
        notifications.add(Notification(R.drawable.route, "Nova Rota", "Há 10 tempo", "Foi adicionado a uma nova rota para Itália, por Bruno Almeida, com saída a 2 de fevereiro.", "Itália"))
        notifications.add(Notification(R.drawable.route, "Nova Rota", "Há 10 tempo", "Foi adicionado a uma nova rota para Itália, por Bruno Almeida, com saída a 2 de fevereiro.", "Itália"))
        notifications.add(Notification(R.drawable.route, "Nova Rota", "Há 10 tempo", "Foi adicionado a uma nova rota para Itália, por Bruno Almeida, com saída a 2 de fevereiro.", "Itália"))
        notifications.add(Notification(R.drawable.route, "Nova Rota", "Há 10 tempo", "Foi adicionado a uma nova rota para Itália, por Bruno Almeida, com saída a 2 de fevereiro.", "Itália"))
        notifications.add(Notification(R.drawable.route, "Nova Rota", "Há 10 tempo", "Foi adicionado a uma nova rota para Itália, por Bruno Almeida, com saída a 2 de fevereiro.", "Itália"))
        notifications.add(Notification(R.drawable.route, "Nova Rota", "Há 10 tempo", "Foi adicionado a uma nova rota para Itália, por Bruno Almeida, com saída a 2 de fevereiro.", "Itália"))
        notifications.add(Notification(R.drawable.route, "Nova Rota", "Há 10 tempo", "Foi adicionado a uma nova rota para Itália, por Bruno Almeida, com saída a 2 de fevereiro.", "Itália"))
        notifications.add(Notification(R.drawable.route, "Nova Rota", "Há 10 tempo", "Foi adicionado a uma nova rota para Itália, por Bruno Almeida, com saída a 2 de fevereiro.", "Itália"))
        notifications.add(Notification(R.drawable.route, "Nova Rota", "Há 10 tempo", "Foi adicionado a uma nova rota para Itália, por Bruno Almeida, com saída a 2 de fevereiro.", "Itália"))
        notifications.add(Notification(R.drawable.route, "Nova Rota", "Há 10 tempo", "Foi adicionado a uma nova rota para Itália, por Bruno Almeida, com saída a 2 de fevereiro.", "Itália"))
        notifications.add(Notification(R.drawable.route, "Nova Rota", "Há 10 tempo", "Foi adicionado a uma nova rota para Itália, por Bruno Almeida, com saída a 2 de fevereiro.", "Itália"))
        notifications.add(Notification(R.drawable.route, "Nova Rota", "Há 10 tempo", "Foi adicionado a uma nova rota para Itália, por Bruno Almeida, com saída a 2 de fevereiro.", "Itália"))
        return notifications
    }
    data class Notification(val imageResource: Int, val notificationType: String,  val temp: String, val description: String, val country: String)

}



