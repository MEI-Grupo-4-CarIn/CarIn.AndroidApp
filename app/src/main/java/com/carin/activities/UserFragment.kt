package com.carin.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.carin.R
import com.carin.adapter.EmployeesAdapter

class UserFragment : Fragment() {

    private lateinit var adapter: EmployeesAdapter
    private lateinit var employees: List<UserFragment.Employee>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.user_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)

        recyclerView.addItemDecoration(ItemSpacingDecoration(5))

        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

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
        employees.add(Employee(R.drawable.ic_person_blue, "Bruno Ferreira","bruno.ferreira@carin.pt"))
        employees.add(Employee(R.drawable.ic_person_blue, "José Ribeiro","jose.ribeiro@carin.pt"))
        employees.add(Employee(R.drawable.ic_person_blue, "Bruno Ferreira","bruno.ferreira@carin.pt"))
        employees.add(Employee(R.drawable.ic_person_blue, "José Ribeiro","jose.ribeiro@carin.pt"))

        return employees
    }
    data class Employee(val imageResource: Int, val name: String, val email: String)
}