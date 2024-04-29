package com.carin.activities

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.carin.R
import com.carin.adapter.RouteInfoAdapter
import com.carin.adapter.SchedulingAdapter
import com.carin.adapter.UserAdapter

class InfoVehicleActivity : AppCompatActivity() {

    private lateinit var adapterRoute: RouteInfoAdapter
    private lateinit var routesinfo: List<RouteInfo>

    private lateinit var adapterUser: UserAdapter
    private lateinit var users: List<User>

    private lateinit var adapter: SchedulingAdapter
    private lateinit var schedulings: List<Scheduling>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_vehicle)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.addItemDecoration(ItemSpacingDecoration(10))
        recyclerView.layoutManager = LinearLayoutManager(this)

        schedulings = getSchedulings()
        adapter = SchedulingAdapter(schedulings)
        recyclerView.adapter = adapter

        val recyclerView1: RecyclerView = findViewById(R.id.recyclerView1)
        recyclerView1.addItemDecoration(ItemSpacingDecoration(10))

        recyclerView1.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        users = getUsers()
        adapterUser = UserAdapter(users)
        recyclerView1.adapter = adapterUser

        val recyclerView2: RecyclerView = findViewById(R.id.recyclerView2)
        recyclerView2.addItemDecoration(ItemSpacingDecoration(10))

        recyclerView2.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        routesinfo = getRoutes()
        adapterRoute = RouteInfoAdapter(routesinfo)
        recyclerView2.adapter = adapterRoute


        val informationText: TextView = findViewById(R.id.informationText)
        val carImageView: ImageView = findViewById(R.id.carImageView)

        val brandText = intent.getStringExtra("brand_text")
        val carImageByteArray: ByteArray? = intent.getByteArrayExtra("car_image")

        informationText.text = brandText

        if (carImageByteArray != null) {
            val carBitmap = BitmapFactory.decodeByteArray(carImageByteArray, 0, carImageByteArray.size)

            carImageView.setImageBitmap(carBitmap)
        }

        val iconImageView: ImageView = findViewById(R.id.iconImageView)
        iconImageView.setOnClickListener {
            val intent = Intent(this, VehicleActivity::class.java)
            startActivity(intent)
        }

        val optionsIcon = findViewById<ImageView>(R.id.optionsIcon)
        optionsIcon.setOnClickListener { view ->
            showOptionsMenu(view)
        }

        val textViewSeeMore1: TextView = findViewById(R.id.textViewSeeMore1)
        textViewSeeMore1.setOnClickListener {
            val intent = Intent(this, UserActivity::class.java)
            startActivity(intent)
        }

        val textViewSeeMore3: TextView = findViewById(R.id.textViewSeeMore3)
        textViewSeeMore3.setOnClickListener {
            val intent = Intent(this, RouteActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getSchedulings(): List<Scheduling> {

        val schedulings = mutableListOf<Scheduling>()
        schedulings.add(Scheduling("12:00", "Pagamento Seguro", "12:00 - 13:00"))
        schedulings.add(Scheduling("12:00", "Bruno vai buscar carro ao Porto", "12:00 - 13:00"))

        return schedulings
    }
    data class Scheduling(val hour: String, val inf: String, val otherHour: String)

    private fun getUsers(): List<User> {

        val users = mutableListOf<User>()
        users.add(User(R.drawable.ic_person_blue,"Bruno", "Ferreira"))
        users.add(User(R.drawable.ic_person_blue,"Alexandre", "Matos"))

        return users
    }
    data class User(val image: Int, val firstName: String, val lastName: String)

    private fun getRoutes(): List<RouteInfo> {

        val routes = mutableListOf<RouteInfo>()
        routes.add(RouteInfo("Porto", "Luxemburgo", "12 Fevereiro", "INFINITY Vision Qe", "18H30m", "1700"))
        routes.add(RouteInfo("Lisboa", "Luxemburgo", "13 Fevereiro", "INFINITY Vision Qe", "18H30m", "1700"))

        return routes
    }
    data class RouteInfo(val origin: String, val destination: String, val date: String, val brand: String, val hour: String, val km: String)

    private fun showOptionsMenu(view: View) {
        val popupMenu = PopupMenu(this, view as ImageView)
        popupMenu.inflate(R.menu.menu_route)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.edit -> {
                    startActivity(Intent(this, EditVehicleActivity::class.java))
                    true
                }
                R.id.delete -> {
                    showDeleteConfirmationDialog()
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }
    private fun showDeleteConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.delete_confirmation_vehicle, null)
        builder.setView(dialogView)

        val dialog = builder.create()
        dialog.show()

        val btnYes = dialogView.findViewById<Button>(R.id.btnYes)
        btnYes.setOnClickListener {
            // Lógica para eliminar
            // Implemente aqui a lógica para deletar a rota
            dialog.dismiss()
        }

        val btnNo = dialogView.findViewById<Button>(R.id.btnNo)
        btnNo.setOnClickListener {
            dialog.dismiss()
        }
    }
}
