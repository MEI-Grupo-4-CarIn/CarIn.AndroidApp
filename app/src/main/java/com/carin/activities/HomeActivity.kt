package com.carin.activities

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.carin.R
import com.carin.adapter.EmployeesHomeAdapter
import com.carin.adapter.HomeNotificationAdapter
import com.carin.adapter.LatestInformationAdapter
import com.carin.adapter.SchedulingHomeAdapter

class HomeActivity : AppCompatActivity() {

    private var isRotated = false
    private lateinit var adapter: HomeNotificationAdapter
    private lateinit var notifications: List<Notification>

    private lateinit var adapterInformation: LatestInformationAdapter
    private lateinit var latestInformations: List<LatestInformation>

    private lateinit var adapterscheduling: SchedulingHomeAdapter
    private lateinit var schedulings: List<Schedulings>

    private lateinit var adapterEmployee: EmployeesHomeAdapter
    private lateinit var employees: List<Employee>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView1)

        recyclerView.addItemDecoration(ItemSpacingDecoration(10))

        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        notifications = getNotifications()

        adapter = HomeNotificationAdapter(notifications)
        recyclerView.adapter = adapter

        val recyclerView2: RecyclerView = findViewById(R.id.recyclerView)

        recyclerView2.addItemDecoration(ItemSpacingDecoration(10))

        recyclerView2.layoutManager = LinearLayoutManager(this)

        latestInformations = getLatestInformation()

        adapterInformation = LatestInformationAdapter(latestInformations)
        recyclerView2.adapter = adapterInformation

        val recyclerView3: RecyclerView = findViewById(R.id.recyclerView3)

        recyclerView3.addItemDecoration(ItemSpacingDecoration(10))

        recyclerView3.layoutManager = LinearLayoutManager(this)

        schedulings = getSchedulings()

        adapterscheduling = SchedulingHomeAdapter(schedulings)
        recyclerView3.adapter = adapterscheduling

        val recyclerView4: RecyclerView = findViewById(R.id.recyclerView4)

        recyclerView4.addItemDecoration(ItemSpacingDecoration(10))

        recyclerView4.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        employees = getEmployees()

        adapterEmployee = EmployeesHomeAdapter(employees)
        recyclerView4.adapter = adapterEmployee

        val imageViewInfo = findViewById<ImageView>(R.id.imageViewInfo)

        imageViewInfo.setOnClickListener {
            val intent = Intent(this, InfoActivity::class.java)
            startActivity(intent)
        }

        val buttonVehicle: ImageView = findViewById(R.id.buttonVehicle)

        buttonVehicle.setOnClickListener {
        val intent = Intent(this, VehicleActivity::class.java)
            startActivity(intent)
        }

        val moreUsers: TextView = findViewById(R.id.textViewSeeMore3)

        moreUsers.setOnClickListener {
            val intent = Intent(this, UserActivity::class.java)
            overridePendingTransition(R.animator.slide_in_right, R.animator.slide_out_left)
            startActivity(intent)
        }

        val moreNotifications: TextView = findViewById(R.id.textViewSeeMore1)

        moreNotifications.setOnClickListener {
            val intent = Intent(this, NotificationActivity::class.java)
            overridePendingTransition(R.animator.slide_in_right, R.animator.slide_out_left)
            startActivity(intent)
        }

        val buttonRoute: ImageView = findViewById(R.id.buttonRoute)

        buttonRoute.setOnClickListener {
            val intent = Intent(this, RouteActivity::class.java)
            startActivity(intent)
        }

        val buttonPerson: ImageView = findViewById(R.id.buttonPerson)

        buttonPerson.setOnClickListener {
            val intent = Intent(this, InfoUserActivity::class.java)
            startActivity(intent)
        }

        val buttonMore = findViewById<ImageView>(R.id.buttonMore)
        val layoutNewAppointment = findViewById<RelativeLayout>(R.id.layoutNewAppointment)
        val layoutAddRoute = findViewById<RelativeLayout>(R.id.layoutAddRoute)
        val layoutAddVehicle = findViewById<RelativeLayout>(R.id.layoutAddVehicle)
        val layoutAddUser = findViewById<RelativeLayout>(R.id.layoutAddUser)

        buttonMore.setOnClickListener {
            if (isRotated) {
                val rotateAnimator = ObjectAnimator.ofFloat(buttonMore, "rotation", 45f, 0f)
                    .apply {
                        duration = 500
                        interpolator = AccelerateDecelerateInterpolator()
                    }

                val animatorSet = AnimatorSet()
                animatorSet.play(rotateAnimator)
                animatorSet.start()

                layoutNewAppointment.visibility = View.INVISIBLE
                layoutAddRoute.visibility = View.INVISIBLE
                layoutAddVehicle.visibility = View.INVISIBLE
                layoutAddUser.visibility = View.INVISIBLE
            } else {
                val rotateAnimator = ObjectAnimator.ofFloat(buttonMore, "rotation", 0f, 45f)
                    .apply {
                        duration = 500
                        interpolator = AccelerateDecelerateInterpolator()
                    }

                val animatorSet = AnimatorSet()
                animatorSet.play(rotateAnimator)
                animatorSet.start()

                layoutNewAppointment.visibility = View.VISIBLE
                layoutAddRoute.visibility = View.VISIBLE
                layoutAddVehicle.visibility = View.VISIBLE
                layoutAddUser.visibility = View.VISIBLE
            }
            isRotated = !isRotated
        }

        layoutAddUser.setOnClickListener {
            val intent = Intent(this, NewUserActivity::class.java)
            startActivity(intent)
        }


        layoutAddVehicle.setOnClickListener {
            val intent = Intent(this, NewVehicleActivity::class.java)
            startActivity(intent)
        }

        layoutAddRoute.setOnClickListener {
            val intent = Intent(this, NewRouteActivity::class.java)
            startActivity(intent)
        }

        layoutNewAppointment.setOnClickListener {
            val intent = Intent(this, NewSchedulingActivity::class.java)
            startActivity(intent)
        }

    }

    private fun getNotifications(): List<Notification> {

        val notifications = mutableListOf<Notification>()
        notifications.add(Notification(R.drawable.route, "Nova Rota","Itália", "Há 2 dias"))
        notifications.add(Notification(R.drawable.route, "Nova Rota","Itália", "Há 2 dias"))
        notifications.add(Notification(R.drawable.route, "Nova Rota","Itália", "Há 2 dias"))
        notifications.add(Notification(R.drawable.route, "Nova Rota","Itália", "Há 2 dias"))

        return notifications
    }
    data class Notification(val imageResource: Int, val notificationType: String,  val country: String, val temp: String)

    private fun getLatestInformation(): List<LatestInformation> {

        val latestInformations = mutableListOf<LatestInformation>()
        latestInformations.add(LatestInformation(R.drawable.ic_check, "Rota Concluída","Bruno Joaquim concluiu a viagem até Itália."))
        latestInformations.add(LatestInformation(R.drawable.ic_check, "Rota Concluída","Bruno Joaquim concluiu a viagem até Itália."))
        latestInformations.add(LatestInformation(R.drawable.ic_check, "Rota Concluída","Bruno Joaquim concluiu a viagem até Itália."))
        latestInformations.add(LatestInformation(R.drawable.ic_check, "Rota Concluída","Bruno Joaquim concluiu a viagem até Itália."))

        return latestInformations
    }
    data class LatestInformation(val icon: Int, val typeInformation: String,  val information: String)

    private fun getSchedulings(): List<Schedulings> {

        val schedulings = mutableListOf<Schedulings>()
        schedulings.add(Schedulings("12:00", "Pagamento Seguro", "12:00 - 13:00"))
        schedulings.add(Schedulings("12:00", "Bruno vai buscar carro ao Porto", "12:00 - 13:00"))
        schedulings.add(Schedulings("12:00", "Pagamento Seguro", "12:00 - 13:00"))
        schedulings.add(Schedulings("12:00", "Bruno vai buscar carro ao Porto", "12:00 - 13:00"))

        return schedulings
    }
    data class Schedulings(val hour: String, val inf: String, val otherHour: String)

    private fun getEmployees(): List<Employee> {

        val employees = mutableListOf<Employee>()
        employees.add(Employee(R.drawable.ic_person_blue, "Bruno Ferreira", "Condutor", "brunoferreira@empresa.pt", "26 de fevereiro de 2002", "145", "50d12h30m", "25000"))
        employees.add(Employee(R.drawable.ic_person_blue, "Bruno Ferreira", "Condutor", "brunoferreira@empresa.pt", "26 de fevereiro de 2002", "145", "50d12h30m", "25000"))
        employees.add(Employee(R.drawable.ic_person_blue, "Bruno Ferreira", "Condutor", "brunoferreira@empresa.pt", "26 de fevereiro de 2002", "145", "50d12h30m", "25000"))
        employees.add(Employee(R.drawable.ic_person_blue, "Bruno Ferreira", "Condutor", "brunoferreira@empresa.pt", "26 de fevereiro de 2002", "145", "50d12h30m", "25000"))
        return employees
    }
    data class Employee(val image: Int, val name: String,  val function: String, val email: String, val birthday: String, val journeys: String, val hours: String, val kms: String)

}



