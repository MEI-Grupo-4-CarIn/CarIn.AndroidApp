package com.carin.activities

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Rect
import android.location.Geocoder
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.carin.R
import com.carin.adapter.DriverSelectionAdapter
import com.carin.adapter.VehicleSelectionAdapter
import com.carin.di.RepositoryModule
import com.carin.domain.enums.Role
import com.carin.domain.enums.RouteStatus
import com.carin.domain.enums.VehicleStatus
import com.carin.domain.models.RouteModel
import com.carin.domain.models.RouteUpdateModel
import com.carin.domain.models.UserModel
import com.carin.domain.models.VehicleModel
import com.carin.utils.ConvertersUtils
import com.carin.utils.NetworkUtils
import com.carin.utils.Resource
import com.carin.viewmodels.InfoRouteViewModel
import com.carin.viewmodels.InfoRouteViewModelFactory
import com.carin.viewmodels.InfoUserViewModel
import com.carin.viewmodels.InfoUserViewModelFactory
import com.carin.viewmodels.InfoVehicleViewModel
import com.carin.viewmodels.InfoVehicleViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class EditRouteActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var infoUserViewModel: InfoUserViewModel
    private lateinit var infoVehicleViewModel: InfoVehicleViewModel
    private lateinit var infoRouteViewModel: InfoRouteViewModel
    private lateinit var routeModel: RouteModel
    private val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    private var selectedRouteStatus: RouteStatus? = null
    private var routeStatusStrings: List<String> = emptyList()
    private var selectedUserId: Int? = null
    private var selectedVehicleId: String? = null
    private var selectedDate: Date? = null
    private var startPoint: LatLng? = null
    private var endPoint: LatLng? = null
    private var searchJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_route)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val userRepository = RepositoryModule.provideUserRepository(this)
        val routeRepository = RepositoryModule.provideRouteRepository(this)
        val vehicleRepository = RepositoryModule.provideVehicleRepository(this)

        val infoUserFactory = InfoUserViewModelFactory(userRepository, routeRepository)
        infoUserViewModel = ViewModelProvider(this, infoUserFactory)[InfoUserViewModel::class.java]

        val infoVehicleFactory = InfoVehicleViewModelFactory(vehicleRepository, routeRepository)
        infoVehicleViewModel = ViewModelProvider(this, infoVehicleFactory)[InfoVehicleViewModel::class.java]

        val infoRouteFactory = InfoRouteViewModelFactory(routeRepository)
        infoRouteViewModel = ViewModelProvider(this, infoRouteFactory)[InfoRouteViewModel::class.java]

        val closeImgView = findViewById<ImageView>(R.id.closeImgView)
        val editTextDepartureCity = findViewById<EditText>(R.id.editTextDepartureCity)
        val editTextDepartureCountry = findViewById<EditText>(R.id.editTextDepartureCountry)
        val editTextDestinationCity = findViewById<EditText>(R.id.editTextDestinationCity)
        val editTextDestinationCountry = findViewById<EditText>(R.id.editTextDestinationCountry)
        val spinnerOptionsRouteStatus = findViewById<Spinner>(R.id.spinnerOptionsRouteStatus)
        val editTextDriver = findViewById<EditText>(R.id.editTextDriver)
        val editTextDepartureDate = findViewById<EditText>(R.id.editTextDepartureDate)
        val editTextVehicle = findViewById<EditText>(R.id.editTextVehicle)
        val avoidTollsCheckBox = findViewById<CheckBox>(R.id.checkboxAvoidTolls)
        val avoidHighwaysCheckBox = findViewById<CheckBox>(R.id.checkboxAvoidHighways)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val errorTextView = findViewById<TextView>(R.id.errorTextView)
        val buttonSaveRoute = findViewById<View>(R.id.buttonSaveRoute)

        editTextDriver.setOnClickListener {
            showSelectionDialog(true)
        }

        editTextVehicle.setOnClickListener {
            showSelectionDialog(false)
        }

        closeImgView.setOnClickListener {
            finish()
        }

        editTextDepartureDate.setOnClickListener {
            showDateTimePickerDialog(editTextDepartureDate)
        }

        setupRouteStatusSpinner(spinnerOptionsRouteStatus)

        infoRouteViewModel.uiDetailsState.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    errorTextView.visibility = View.GONE
                }
                is Resource.Success -> {
                    resource.data?.let { route ->
                        routeModel = route

                        editTextDepartureCity.setText(route.startPoint.city)
                        editTextDepartureCountry.setText(route.startPoint.country)
                        editTextDestinationCity.setText(route.endPoint.city)
                        editTextDestinationCountry.setText(route.endPoint.country)
                        editTextDriver.setText("${route.user?.firstName} ${route.user?.lastName} (${route.user?.email})")
                        selectedUserId = route.user?.id
                        editTextDepartureDate.setText(formatter.format(route.startDate))
                        selectedDate = route.startDate
                        editTextVehicle.setText("${route.vehicle?.brand} ${route.vehicle?.model} (${route.vehicle?.licensePlate})")
                        selectedVehicleId = route.vehicle?.id
                        avoidTollsCheckBox.isChecked = route.avoidTolls
                        avoidHighwaysCheckBox.isChecked = route.avoidHighways

                        val initialStatus = route.status
                        val initialPosition = routeStatusStrings.indexOf(
                            getString(resources.getIdentifier(initialStatus.stringKey, "string", packageName))
                        )
                        if (initialPosition >= 0) {
                            spinnerOptionsRouteStatus.setSelection(initialPosition)
                        }
                    }

                    setUpMapWithLocations()
                    progressBar.visibility = View.GONE
                }
                is Resource.Error -> {
                    progressBar.visibility = View.GONE
                    errorTextView.visibility = View.VISIBLE
                }
            }
        }

        infoRouteViewModel.loadRouteDetails(intent.getStringExtra("routeId") ?: "")

        buttonSaveRoute.setOnClickListener {
            val isNetworkAvailable = NetworkUtils.isNetworkAvailable(this)
            if (isNetworkAvailable) {
                errorTextView.visibility = View.VISIBLE
            } else {
                errorTextView.visibility = View.GONE
            }

            if (validateInputs(editTextDepartureDate) && isNetworkAvailable) {
                val updatedUserId = if (selectedUserId != routeModel.user?.id) selectedUserId else null
                val updatedVehicleId = if (selectedVehicleId != routeModel.vehicle?.id) selectedVehicleId else null
                val updatedStartDate = if (selectedDate != routeModel.startDate) ConvertersUtils.convertBirthDateToCSharpFormat(editTextDepartureDate.text.toString()) else null
                val updatedRouteStatus = if (selectedRouteStatus != routeModel.status) selectedRouteStatus else null
                val updatedAvoidTolls = if (avoidTollsCheckBox.isChecked != routeModel.avoidTolls) avoidTollsCheckBox.isChecked else null
                val updatedAvoidHighways = if (avoidHighwaysCheckBox.isChecked != routeModel.avoidHighways) avoidHighwaysCheckBox.isChecked else null

                if (hasChanges(updatedUserId, updatedVehicleId, updatedStartDate, updatedRouteStatus, updatedAvoidTolls, updatedAvoidHighways)) {
                    val routeUpdateModel = RouteUpdateModel(
                        id = routeModel.id,
                        userId = updatedUserId,
                        vehicleId = updatedVehicleId,
                        startDate = updatedStartDate,
                        status = updatedRouteStatus,
                        avoidTolls = updatedAvoidTolls,
                        avoidHighways = updatedAvoidHighways
                    )

                    infoRouteViewModel.updateRoute(routeUpdateModel)
                }
            }
        }

        setUpMapWithLocations()

        infoRouteViewModel.routeUpdateState.observe(this) { result ->
            when (result) {
                is Resource.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    errorTextView.visibility = View.GONE
                }
                is Resource.Success -> {
                    progressBar.visibility = View.GONE
                    errorTextView.visibility = View.GONE

                    val successMessage = getString(R.string.route_updated_success)
                    Toast.makeText(this, successMessage, Toast.LENGTH_SHORT).show()
                    finish()
                }
                is Resource.Error -> {
                    progressBar.visibility = View.GONE
                    errorTextView.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
    }

    private fun setUpMapWithLocations() {
        val editTextDepartureCity = findViewById<EditText>(R.id.editTextDepartureCity).text.toString()
        val editTextDepartureCountry = findViewById<EditText>(R.id.editTextDepartureCountry).text.toString()
        val editTextDestinationCity = findViewById<EditText>(R.id.editTextDestinationCity).text.toString()
        val editTextDestinationCountry = findViewById<EditText>(R.id.editTextDestinationCountry).text.toString()

        lifecycleScope.launch {
            val startPointDeferred = async { geocodeAddress("$editTextDepartureCity, $editTextDepartureCountry") }
            val endPointDeferred = async { geocodeAddress("$editTextDestinationCity, $editTextDestinationCountry") }

            val results = awaitAll(startPointDeferred, endPointDeferred)

            startPoint = results[0]
            endPoint = results[1]

            drawRoute()
        }
    }

    private suspend fun geocodeAddress(address: String): LatLng? {
        return withContext(Dispatchers.IO) {
            val geocoder = Geocoder(this@EditRouteActivity, Locale.getDefault())
            try {
                val addresses = geocoder.getFromLocationName(address, 1)
                if (!addresses.isNullOrEmpty()) {
                    val location = addresses[0]
                    LatLng(location.latitude, location.longitude)
                } else {
                    null
                }
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }
    }

    private fun drawRoute() {
        mMap.clear()
        startPoint?.let { start ->
            mMap.addMarker(MarkerOptions().position(start).title("Start Point"))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(start, 12f))

            endPoint?.let { end ->
                mMap.addMarker(MarkerOptions().position(end).title("End Point"))

                val routePoints: List<LatLng> = listOf(start, end)
                val polylineOptions = PolylineOptions().addAll(routePoints)
                mMap.addPolyline(polylineOptions)
                val bounds = LatLngBounds.Builder().include(start).include(end).build()
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
            }
        }
    }

    private fun setupRouteStatusSpinner(spinner: Spinner) {
        routeStatusStrings = RouteStatus.entries.map {
            getString(resources.getIdentifier(it.stringKey, "string", packageName))
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, routeStatusStrings)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // Get the selected string
                val selectedString = parent.getItemAtPosition(position).toString()

                // Find the corresponding RouteStatus enum value
                selectedRouteStatus = RouteStatus.entries.find {
                    getString(resources.getIdentifier(it.stringKey, "string", packageName)) == selectedString
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }

    private fun showDateTimePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()

        selectedDate?.let {
            calendar.time = it
        }

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                val timePickerDialog = TimePickerDialog(
                    this,
                    { _, selectedHourOfDay, selectedMinute ->
                        val selectedDateTime = Calendar.getInstance()
                        selectedDateTime.set(
                            selectedYear,
                            selectedMonth,
                            selectedDayOfMonth,
                            selectedHourOfDay,
                            selectedMinute
                        )
                        selectedDate = selectedDateTime.time
                        val selectedDateTimeFormatted =
                            SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(
                                selectedDateTime.time
                            )
                        editText.setText(selectedDateTimeFormatted)
                    },
                    hourOfDay,
                    minute,
                    true
                )
                timePickerDialog.show()
            },
            year, month, dayOfMonth
        )

        datePickerDialog.show()
    }

    private fun showSelectionDialog(isDriverSelection: Boolean) {
        val dialogView = if (isDriverSelection) {
            LayoutInflater.from(this).inflate(R.layout.dialog_driver_selection, null)
        } else {
            LayoutInflater.from(this).inflate(R.layout.dialog_vehicle_selection, null)
        }
        val searchEditText = dialogView.findViewById<EditText>(R.id.searchEditText)
        val selectionRecyclerView = dialogView.findViewById<RecyclerView>(R.id.selectionRecyclerView)
        val closeIcon = dialogView.findViewById<ImageView>(R.id.closeIcon)
        val progressBar = dialogView.findViewById<ProgressBar>(R.id.progressBar)
        val emptyTextView = dialogView.findViewById<TextView>(R.id.emptyTextView)
        val errorTextView = dialogView.findViewById<TextView>(R.id.errorTextView)

        val userDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        if (isDriverSelection) {
            var driverList = listOf<UserModel>()

            selectionRecyclerView.layoutManager = LinearLayoutManager(this)
            val driverSelectionAdapter = DriverSelectionAdapter(driverList) { user ->
                selectedUserId = user.id
                val editTextDriver = findViewById<EditText>(R.id.editTextDriver)
                editTextDriver.setText("${user.firstName} ${user.lastName} (${user.email})")
                userDialog.dismiss()
            }
            selectionRecyclerView.adapter = driverSelectionAdapter

            userDialog.show()

            // Load users when the dialog is shown
            infoUserViewModel.loadUsers(Role.Driver, null, 1, 8)

            closeIcon.setOnClickListener {
                userDialog.dismiss()
            }

            infoUserViewModel.uiUsersState.observe(this) { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        progressBar.visibility = View.VISIBLE
                        errorTextView.visibility = View.GONE
                        emptyTextView.visibility = View.GONE
                    }
                    is Resource.Success -> {
                        errorTextView.visibility = View.GONE

                        driverList = resource.data ?: emptyList()
                        if (driverList.isEmpty()) {
                            emptyTextView.visibility = View.VISIBLE
                        } else {
                            emptyTextView.visibility = View.GONE
                        }
                        driverSelectionAdapter.updateDrivers(driverList)

                        progressBar.visibility = View.GONE
                    }
                    is Resource.Error -> {
                        emptyTextView.visibility = View.GONE
                        errorTextView.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE
                    }
                }
            }
        } else {
            var vehiclesList = listOf<VehicleModel>()

            selectionRecyclerView.layoutManager = LinearLayoutManager(this)
            val driverSelectionAdapter = VehicleSelectionAdapter(this, vehiclesList) { vehicle ->
                selectedVehicleId = vehicle.id
                val editTextVehicle = findViewById<EditText>(R.id.editTextVehicle)
                editTextVehicle.setText("${vehicle.brand} ${vehicle.model} (${vehicle.licensePlate})")
                userDialog.dismiss()
            }
            selectionRecyclerView.adapter = driverSelectionAdapter

            userDialog.show()

            // Load vehicles when the dialog is shown
            infoVehicleViewModel.loadVehicles(VehicleStatus.None, null, 1, 8)

            closeIcon.setOnClickListener {
                userDialog.dismiss()
            }

            infoVehicleViewModel.uiVehiclesState.observe(this) { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        progressBar.visibility = View.VISIBLE
                        errorTextView.visibility = View.GONE
                        emptyTextView.visibility = View.GONE
                    }
                    is Resource.Success -> {
                        errorTextView.visibility = View.GONE

                        vehiclesList = resource.data ?: emptyList()
                        if (vehiclesList.isEmpty()) {
                            emptyTextView.visibility = View.VISIBLE
                        } else {
                            emptyTextView.visibility = View.GONE
                        }
                        driverSelectionAdapter.updateVehicles(vehiclesList)

                        progressBar.visibility = View.GONE
                    }
                    is Resource.Error -> {
                        emptyTextView.visibility = View.GONE
                        errorTextView.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE
                    }
                }
            }
        }

        // Add a text watcher to handle text changes
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchJob?.cancel()
                searchJob = lifecycleScope.launch {
                    delay(500)
                    s?.let {
                        if (isDriverSelection) {
                            infoUserViewModel.loadUsers(Role.Driver, it.toString(), 1, 8)
                        } else {
                            infoVehicleViewModel.loadVehicles(VehicleStatus.None, it.toString(), 1, 8)
                        }
                    }
                }
            }
        })

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            // Close the keyboard when the search button is clicked
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (isDriverSelection) {
                    infoUserViewModel.loadUsers(Role.Driver, searchEditText.text.toString(), 1, 8)
                } else {
                    infoVehicleViewModel.loadVehicles(VehicleStatus.None, searchEditText.text.toString(), 1, 8)
                }
                searchEditText.clearFocus()
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
                true
            } else {
                false
            }
        }
    }

    private fun validateInputs(
        editTextDepartureDate: EditText,
    ): Boolean {
        val errorMessage = getString(R.string.date_should_be_future)
        var isValid = true

        if (selectedDate != null && selectedDate!! < Date()) {
            editTextDepartureDate.error = errorMessage
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            isValid = false
        }

        return isValid
    }

    private fun hasChanges(
        updatedUserId: Int?,
        updatedVehicleId: String?,
        updatedStartDate: String?,
        updatedRouteStatus: RouteStatus?,
        updatedAvoidTolls: Boolean?,
        updatedAvoidHighways: Boolean?
    ): Boolean {
        return updatedUserId != null ||
                updatedVehicleId != null ||
                updatedStartDate != null ||
                updatedRouteStatus != null ||
                updatedAvoidTolls != null ||
                updatedAvoidHighways != null
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, R.animator.slide_out_down)
    }
}
