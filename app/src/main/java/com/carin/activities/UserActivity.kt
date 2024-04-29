package com.carin.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.carin.R
import com.carin.adapter.EmployeesAdapter

class UserActivity : AppCompatActivity() {

    private lateinit var adapter: EmployeesAdapter
    private lateinit var employees: List<Employee>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)

        recyclerView.layoutManager = GridLayoutManager(this, 2)

        recyclerView.addItemDecoration(ItemSpacingDecoration(10))

        employees = getEmployees()

        adapter = EmployeesAdapter(employees)
        recyclerView.adapter = adapter
    }


    private fun getEmployees(): List<Employee> {

        val employees = mutableListOf<Employee>()
        employees.add(Employee(R.drawable.ic_person_blue, "Bruno Ferreira","bruno.ferreira@carin.pt"))
        employees.add(Employee(R.drawable.ic_person_blue, "José Ribeiro","jose.ribeiro@carin.pt"))
        employees.add(Employee(R.drawable.ic_person_blue, "Bruno Ferreira","bruno.ferreira@carin.pt"))
        employees.add(Employee(R.drawable.ic_person_blue, "José Ribeiro","jose.ribeiro@carin.pt"))

        return employees
    }
    data class Employee(val imageResource: Int, val name: String, val email: String)
}
