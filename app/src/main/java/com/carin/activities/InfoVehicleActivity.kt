package com.carin.activities

import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.carin.R
import com.carin.adapter.RouteInfoAdapter
import com.carin.adapter.SchedulingAdapter
import com.carin.adapter.UserAdapter
import com.carin.di.RepositoryModule
import com.carin.domain.enums.Role
import com.carin.utils.AuthUtils
import com.carin.utils.ItemSpacingDecoration
import com.carin.utils.Resource
import com.carin.utils.getStringResourceByName
import com.carin.viewmodels.InfoVehicleViewModel
import com.carin.viewmodels.InfoVehicleViewModelFactory
import com.carin.viewmodels.states.RoutesListState
import java.text.SimpleDateFormat
import java.util.Locale

class InfoVehicleActivity : AppCompatActivity() {

    private lateinit var viewModel: InfoVehicleViewModel
    private lateinit var vehicleId: String
    private lateinit var adapterRoute: RouteInfoAdapter
    private lateinit var adapterUser: UserAdapter
    private lateinit var users: List<User>
    private lateinit var adapter: SchedulingAdapter
    private lateinit var schedulings: List<Scheduling>
    private lateinit var progressBar: ProgressBar
    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_vehicle)

        val userAuth = AuthUtils.getUserAuth(this)
        userAuth?.let {
            adjustUIBasedOnRole(it.role)
            if (it.role == Role.Manager) {
                prepareOptionsMenu()
            }
        }

        val vehicleIdFromIntent = intent.getStringExtra("vehicleId") ?: ""
        vehicleId = vehicleIdFromIntent

        val infoVehicleGoBackIcon: ImageView = findViewById(R.id.infoVehicleGoBackIcon)
        infoVehicleGoBackIcon.setOnClickListener {
            finish()
        }

        val textViewSeeMoreAppointments = findViewById<TextView>(R.id.textViewSeeMoreAppointments)
        textViewSeeMoreAppointments.setOnClickListener {
            val intent = Intent(this, CalendarActivity::class.java)
            overridePendingTransition(R.animator.slide_in_right, R.animator.slide_out_left)
            startActivity(intent)
        }

        val textViewSeeMore1: TextView = findViewById(R.id.textViewSeeMore1)
        textViewSeeMore1.setOnClickListener {
            val intent = Intent(this, UsersListActivity::class.java)
            startActivity(intent)
        }

        val textViewSeeMore3: TextView = findViewById(R.id.textViewSeeMore3)
        textViewSeeMore3.setOnClickListener {
            val intent = Intent(this, RoutesListActivity::class.java)
            startActivity(intent)
        }

        val vehicleImage = findViewById<ImageView>(R.id.vehicleImage)
        val infoVehicleBrandTxt = findViewById<TextView>(R.id.infoVehicleBrandTxt)
        val infoVehicleModelTxt = findViewById<TextView>(R.id.infoVehicleModelTxt)
        val infoVehicleLicensePlateTxt = findViewById<TextView>(R.id.infoVehicleLicensePlateTxt)
        val infoVehicleVinTxt = findViewById<TextView>(R.id.infoVehicleVinTxt)
        val infoVehicleColorTxt = findViewById<TextView>(R.id.infoVehicleColorTxt)
        val infoVehicleDateRegistrationTxt = findViewById<TextView>(R.id.infoVehicleDateRegistrationTxt)
        val infoVehicleDateAcquisitionTxt = findViewById<TextView>(R.id.infoVehicleDateAcquisitionTxt)
        val infoVehicleConsumptionTxt = findViewById<TextView>(R.id.infoVehicleConsumptionTxt)
        val infoVehicleSeatsTxt = findViewById<TextView>(R.id.infoVehicleSeatsTxt)
        val infoVehicleFuelTxt = findViewById<TextView>(R.id.infoVehicleFuelTxt)
        val infoVehicleKilometersTxt = findViewById<TextView>(R.id.infoVehicleKilometersTxt)
        val infoVehicleClassTxt = findViewById<TextView>(R.id.infoVehicleClassTxt)
        progressBar = findViewById(R.id.progressBar)
        val errorTextView = findViewById<TextView>(R.id.errorTextView)

        val vehicleRepository = RepositoryModule.provideVehicleRepository(this)
        val routeRepository = RepositoryModule.provideRouteRepository(this)
        val factory = InfoVehicleViewModelFactory(vehicleRepository, routeRepository)
        viewModel = ViewModelProvider(this, factory)[InfoVehicleViewModel::class.java]
        viewModel.uiDetailsState.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    errorTextView.visibility = View.GONE
                }
                is Resource.Success -> {
                    errorTextView.visibility = View.GONE

                    resource.data?.let { vehicle ->
                        vehicleImage.setImageResource(vehicle.imageResource)
                        infoVehicleBrandTxt.text = vehicle.brand
                        infoVehicleModelTxt.text = vehicle.model
                        infoVehicleLicensePlateTxt.text = vehicle.licensePlate
                        infoVehicleVinTxt.text = vehicle.vin
                        infoVehicleColorTxt.text = vehicle.color
                        infoVehicleDateRegistrationTxt.text = dateFormatter.format(vehicle.registerDate)
                        infoVehicleDateAcquisitionTxt.text = dateFormatter.format(vehicle.acquisitionDate)
                        infoVehicleConsumptionTxt.text = "${vehicle.averageFuelConsumption} l/100km"
                        infoVehicleSeatsTxt.text = vehicle.capacity.toInt().toString()
                        infoVehicleFuelTxt.text = this.getStringResourceByName(vehicle.fuelType.stringKey)
                        infoVehicleKilometersTxt.text = "${vehicle.kms.toInt()} km"
                        infoVehicleClassTxt.text = vehicle.category
                    }

                    progressBar.visibility = View.GONE
                }
                is Resource.Error -> {
                    progressBar.visibility = View.GONE
                    errorTextView.visibility = View.VISIBLE
                    val networkErrorMessage = getString(R.string.network_error)
                    Toast.makeText(this, networkErrorMessage, Toast.LENGTH_LONG).show()
                }
            }
        }

        prepareRecyclerViews()
        prepareRoutesRecyclerView()

        viewModel.loadVehicleDetails(vehicleId)
    }

    override fun onResume() {
        super.onResume()

        vehicleId.let {
            viewModel.loadVehicleDetails(it)
            viewModel.loadRoutesForVehicle(it)
        }
    }

    private fun prepareOptionsMenu() {
        val optionsIcon = findViewById<ImageView>(R.id.optionsIcon)
        optionsIcon.visibility = View.VISIBLE

        optionsIcon.setOnClickListener { view ->

            val popupMenu = PopupMenu(ContextThemeWrapper(this, R.style.PopupMenu), view)

            val editItem = popupMenu.menu.add(Menu.NONE, R.id.edit, Menu.NONE, "Edit")
            val deleteItem = popupMenu.menu.add(Menu.NONE, R.id.delete, Menu.NONE, "Delete")

            val editIcon = ContextCompat.getDrawable(this, R.drawable.ic_edit)
            editIcon?.setBounds(0, 0, editIcon.intrinsicWidth, editIcon.intrinsicHeight)
            val editIconSpan = editIcon?.let { ImageSpan(it, ImageSpan.ALIGN_BOTTOM) }
            val editItemTitle = SpannableString(" ${getString(R.string.edit)}")
            editItemTitle.setSpan(editIconSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            editItem.title = editItemTitle

            val deleteIcon = ContextCompat.getDrawable(this, R.drawable.ic_delete)
            deleteIcon?.setBounds(0, 0, deleteIcon.intrinsicWidth, deleteIcon.intrinsicHeight)
            val logoutIconSpan = deleteIcon?.let { ImageSpan(it, ImageSpan.ALIGN_BOTTOM) }
            val logoutItemTitle = SpannableString(" ${getString(R.string.delete)}")
            logoutItemTitle.setSpan(logoutIconSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            deleteItem.title = logoutItemTitle

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.edit -> {
                        val intent = Intent(this, EditVehicleActivity::class.java)
                        intent.putExtra("vehicleId", vehicleId)
                        startActivity(intent)
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
    }

    private fun prepareRecyclerViews() {
        val recyclerViewAppointments = findViewById<RecyclerView>(R.id.recyclerViewAppointments)
        recyclerViewAppointments.addItemDecoration(ItemSpacingDecoration(10))
        recyclerViewAppointments.layoutManager = LinearLayoutManager(this)

        schedulings = getSchedulings()
        adapter = SchedulingAdapter(schedulings)
        recyclerViewAppointments.adapter = adapter

        adapter.setOnItemClickListener(object : SchedulingAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val intent = Intent(this@InfoVehicleActivity, InfoSchedulingActivity::class.java)
                startActivity(intent)
            }
        })

        val recyclerViewLastDrivers = findViewById<RecyclerView>(R.id.recyclerViewLastDrivers)
        recyclerViewLastDrivers.addItemDecoration(ItemSpacingDecoration(10))
        recyclerViewLastDrivers.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        users = getUsers()
        adapterUser = UserAdapter(users)
        recyclerViewLastDrivers.adapter = adapterUser

        adapterUser.setOnItemClickListener(object : UserAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val intent = Intent(this@InfoVehicleActivity, InfoUserActivity::class.java)
                startActivity(intent)
            }
        })
    }

    private fun prepareRoutesRecyclerView() {
        val recyclerViewRoutes = findViewById<RecyclerView>(R.id.recyclerViewRoutes)
        val emptyTextViewRoutes = findViewById<TextView>(R.id.emptyTextViewRoutes)

        recyclerViewRoutes.addItemDecoration(ItemSpacingDecoration(10))
        recyclerViewRoutes.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        adapterRoute = RouteInfoAdapter(mutableListOf())
        recyclerViewRoutes.adapter = adapterRoute

        viewModel.uiRoutesState.observe(this) { state ->
            when (state) {
                is RoutesListState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    emptyTextViewRoutes.visibility = View.GONE
                }
                is RoutesListState.Success -> {
                    if (state.isEmpty) {
                        emptyTextViewRoutes.visibility = View.VISIBLE
                        recyclerViewRoutes.visibility = View.GONE
                    } else {
                        emptyTextViewRoutes.visibility = View.GONE
                        recyclerViewRoutes.visibility = View.VISIBLE
                        adapterRoute.updateRoutes(state.routes)
                    }

                    progressBar.visibility = View.GONE
                }
                is RoutesListState.Error -> {
                    emptyTextViewRoutes.visibility = View.GONE
                    progressBar.visibility = View.GONE
                }
            }
        }

        viewModel.loadRoutesForVehicle(vehicleId)
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
        findViewById<View>(R.id.thirdLinearLayout).visibility = View.GONE
        findViewById<View>(R.id.recyclerViewAppointments).visibility = View.GONE
        findViewById<View>(R.id.line2LinearLayout).visibility = View.GONE
        findViewById<View>(R.id.fourLinearLayout).visibility = View.GONE
        findViewById<View>(R.id.recyclerViewLastDrivers).visibility = View.GONE
        findViewById<View>(R.id.line3LinearLayout).visibility = View.GONE
        findViewById<View>(R.id.fiveLinearLayout).visibility = View.GONE
        findViewById<View>(R.id.recyclerViewRoutes).visibility = View.GONE
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

    private fun showDeleteConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.delete_confirmation_vehicle, null)
        builder.setView(dialogView)

        val dialog = builder.create()
        dialog.show()

        val btnYes = dialogView.findViewById<Button>(R.id.btnYes)
        btnYes.setOnClickListener {
            // Implement delete logic
            dialog.dismiss()
        }

        val btnNo = dialogView.findViewById<Button>(R.id.btnNo)
        btnNo.setOnClickListener {
            dialog.dismiss()
        }
    }
}
