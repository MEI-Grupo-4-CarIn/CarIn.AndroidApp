package com.carin.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CalendarView
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.carin.R
import com.carin.adapter.SchedulingHomeAdapter
import com.carin.domain.enums.Role
import com.carin.utils.AuthUtils
import java.util.Calendar

class CalendarActivity : AppCompatActivity() {

    private lateinit var adapterscheduling: SchedulingHomeAdapter
    private lateinit var schedulings: List<HomeActivity.Schedulings>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        val userAuths = AuthUtils.getUserAuth(this)

        userAuths?.let {
            adjustUIBasedOnRole(it.role)
        }

        val calendarView = findViewById<CalendarView>(R.id.calendarView)

        val currentDate = Calendar.getInstance()
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val day = currentDate.get(Calendar.DAY_OF_MONTH)

        calendarView.setDate(currentDate.timeInMillis)

        val iconImage: ImageView = findViewById(R.id.iconImage)

        iconImage.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            overridePendingTransition(R.animator.slide_in_right, R.animator.slide_out_left)
            startActivity(intent)
        }

        val createSchedulings: ImageView = findViewById(R.id.optionsIcon)

        createSchedulings.setOnClickListener {
            val intent = Intent(this, NewSchedulingActivity::class.java)
            overridePendingTransition(R.animator.slide_in_right, R.animator.slide_out_left)
            startActivity(intent)
        }

        val recyclerView3: RecyclerView = findViewById(R.id.recyclerView3)

        recyclerView3.addItemDecoration(ItemSpacingDecoration(10))

        recyclerView3.layoutManager = LinearLayoutManager(this)

        schedulings = getScheduling()

        adapterscheduling = SchedulingHomeAdapter(schedulings)
        recyclerView3.adapter = adapterscheduling

        adapterscheduling.setOnItemClickListener(object : SchedulingHomeAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val intent = Intent(this@CalendarActivity, InfoSchedulingActivity::class.java)
                startActivity(intent)
            }
        })

        val recyclerView4: RecyclerView = findViewById(R.id.recyclerView4)

        recyclerView4.addItemDecoration(ItemSpacingDecoration(10))

        recyclerView4.layoutManager = LinearLayoutManager(this)

        schedulings = getScheduling()

        adapterscheduling = SchedulingHomeAdapter(schedulings)
        recyclerView4.adapter = adapterscheduling

        adapterscheduling.setOnItemClickListener(object : SchedulingHomeAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val intent = Intent(this@CalendarActivity, InfoSchedulingActivity::class.java)
                startActivity(intent)
            }
        })
    }

    private fun adjustUIBasedOnRole(role: Role) {
        when (role) {
            Role.Admin -> showAdminComponents()
            Role.Manager -> showManagerComponents()
            Role.Driver -> showDriverComponents()
        }
    }

    private fun showAdminComponents() {

    }
    private fun showManagerComponents() {

    }

    private fun showDriverComponents() {
        findViewById<View>(R.id.optionsIcon).visibility = View.GONE
    }

    private fun getScheduling(): List<HomeActivity.Schedulings> {

        val schedulings = mutableListOf<HomeActivity.Schedulings>()
        schedulings.add(HomeActivity.Schedulings("12:00", "Pagamento Seguro", "12:00 - 13:00"))
        schedulings.add(HomeActivity.Schedulings("12:00", "Bruno vai buscar carro ao Porto", "12:00 - 13:00"))
        schedulings.add(HomeActivity.Schedulings("12:00", "Pagamento Seguro", "12:00 - 13:00"))
        schedulings.add(HomeActivity.Schedulings("12:00", "Bruno vai buscar carro ao Porto", "12:00 - 13:00"))

        return schedulings
    }
}
