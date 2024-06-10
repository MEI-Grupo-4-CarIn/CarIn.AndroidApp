package com.carin.activities

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.carin.R
import com.carin.adapter.DriverSelectionAdapter
import com.carin.adapter.VehicleSelectionAdapter
import com.carin.di.RepositoryModule
import com.carin.domain.enums.Role
import com.carin.domain.enums.VehicleStatus
import com.carin.domain.models.LocationCreationModel
import com.carin.domain.models.RouteCreationModel
import com.carin.domain.models.UserModel
import com.carin.domain.models.VehicleModel
import com.carin.utils.ConvertersUtils
import com.carin.utils.Resource
import com.carin.viewmodels.CreateRouteViewModel
import com.carin.viewmodels.CreateRouteViewModelFactory
import com.carin.viewmodels.InfoUserViewModel
import com.carin.viewmodels.InfoUserViewModelFactory
import com.carin.viewmodels.InfoVehicleViewModel
import com.carin.viewmodels.InfoVehicleViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class NewRouteActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var infoUserViewModel: InfoUserViewModel
    private lateinit var infoVehicleViewModel: InfoVehicleViewModel
    private lateinit var createRouteViewModel: CreateRouteViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private var selectedUserId: Int? = null
    private var selectedVehicleId: String? = null
    private var selectedDate: Date? = null
    private var startPoint: LatLng? = null
    private var endPoint: LatLng? = null
    private var searchJob: Job? = null

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_route)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val userRepository = RepositoryModule.provideUserRepository(this)
        val routeRepository = RepositoryModule.provideRouteRepository(this)
        val vehicleRepository = RepositoryModule.provideVehicleRepository(this)

        val infoUserFactory = InfoUserViewModelFactory(userRepository, routeRepository)
        infoUserViewModel = ViewModelProvider(this, infoUserFactory)[InfoUserViewModel::class.java]

        val infoVehicleFactory = InfoVehicleViewModelFactory(vehicleRepository)
        infoVehicleViewModel = ViewModelProvider(this, infoVehicleFactory)[InfoVehicleViewModel::class.java]

        val createRouteFactory = CreateRouteViewModelFactory(routeRepository)
        createRouteViewModel = ViewModelProvider(this, createRouteFactory)[CreateRouteViewModel::class.java]

        val closeImgView = findViewById<ImageView>(R.id.closeImgView)
        val editTextDepartureCity = findViewById<EditText>(R.id.editTextDepartureCity)
        val editTextDepartureCountry = findViewById<EditText>(R.id.editTextDepartureCountry)
        val editTextDestinationCity = findViewById<EditText>(R.id.editTextDestinationCity)
        val editTextDestinationCountry = findViewById<EditText>(R.id.editTextDestinationCountry)
        val editTextDriver = findViewById<EditText>(R.id.editTextDriver)
        val editTextDepartureDate = findViewById<EditText>(R.id.editTextDepartureDate)
        val editTextVehicle = findViewById<EditText>(R.id.editTextVehicle)
        val avoidTollsCheckBox = findViewById<CheckBox>(R.id.checkboxAvoidTolls)
        val avoidHighwaysCheckBox = findViewById<CheckBox>(R.id.checkboxAvoidHighways)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val buttonCreateRoute = findViewById<View>(R.id.buttonCreateRoute)

        editTextDriver.setOnClickListener {
            showSelectionDialog(true)
        }

        editTextVehicle.setOnClickListener {
            showSelectionDialog(false)
        }

        closeImgView.setOnClickListener {
            finish()
        }

        buttonCreateRoute.setOnClickListener {
            if (validateInputs(
                    editTextDepartureCity,
                    editTextDepartureCountry,
                    editTextDestinationCity,
                    editTextDestinationCountry,
                    editTextDepartureDate,
                    editTextDriver,
                    editTextVehicle)
                ) {
                val routeCreationModel = RouteCreationModel(
                    userId = selectedUserId!!,
                    vehicleId = selectedVehicleId!!,
                    startPoint = LocationCreationModel(editTextDepartureCity.text.toString(), editTextDepartureCountry.text.toString()),
                    endPoint = LocationCreationModel(editTextDestinationCity.text.toString(), editTextDestinationCountry.text.toString()),
                    startDate = ConvertersUtils.convertBirthDateToCSharpFormat(editTextDepartureDate.text.toString()) ?: "",
                    avoidTolls = avoidTollsCheckBox.isChecked,
                    avoidHighways = avoidHighwaysCheckBox.isChecked,
                )

                createRouteViewModel.createRoute(routeCreationModel)
            }
        }

        createRouteViewModel.creationState.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    buttonCreateRoute.isEnabled = false
                    progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    buttonCreateRoute.isEnabled = true
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, getString(R.string.route_created_message), Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, InfoRouteActivity::class.java)
                    intent.putExtra("routeId", resource.data)
                    startActivity(intent)
                    finish()
                }
                is Resource.Error -> {
                    buttonCreateRoute.isEnabled = true
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, resource.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        setupLocationEditTexts()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        setUpMap()
    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        mMap.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 8f))
            }
        }
    }

    private fun setupLocationEditTexts() {
        val editTextDepartureCity = findViewById<EditText>(R.id.editTextDepartureCity)
        val editTextDepartureCountry = findViewById<EditText>(R.id.editTextDepartureCountry)
        val editTextDestinationCity = findViewById<EditText>(R.id.editTextDestinationCity)
        val editTextDestinationCountry = findViewById<EditText>(R.id.editTextDestinationCountry)
        val editTextDepartureDate = findViewById<EditText>(R.id.editTextDepartureDate)

        editTextDepartureDate.setOnClickListener {
            showDateTimePickerDialog(editTextDepartureDate)
        }

        editTextDepartureCity.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val city = editTextDepartureCity.text.toString()
                val country = editTextDepartureCountry.text.toString()
                if (city.isNotEmpty() && country.isNotEmpty()) {
                    geocodeAddress("$city, $country") { location ->
                        startPoint = location
                        startPoint?.let {
                            drawRoute()
                        } ?: run {
                            val message = getString(R.string.invalid_departure_message)
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        editTextDepartureCountry.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val city = editTextDepartureCity.text.toString()
                val country = editTextDepartureCountry.text.toString()
                if (city.isNotEmpty() && country.isNotEmpty()) {
                    geocodeAddress("$city, $country") { location ->
                        startPoint = location
                        startPoint?.let {
                            drawRoute()
                        } ?: run {
                            val message = getString(R.string.invalid_departure_message)
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    val message = getString(R.string.departure_not_empty_message)
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        editTextDestinationCity.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val city = editTextDestinationCity.text.toString()
                val country = editTextDestinationCountry.text.toString()
                if (city.isNotEmpty() && country.isNotEmpty()) {
                    geocodeAddress("$city, $country") { location ->
                        endPoint = location
                        endPoint?.let {
                            drawRoute()
                        } ?: run {
                            val message = getString(R.string.invalid_departure_message)
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        editTextDestinationCountry.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val city = editTextDestinationCity.text.toString()
                val country = editTextDestinationCountry.text.toString()
                if (city.isNotEmpty() && country.isNotEmpty()) {
                    geocodeAddress("$city, $country") { location ->
                        endPoint = location
                        endPoint?.let {
                            drawRoute()
                        } ?: run {
                            val message = getString(R.string.invalid_destination_message)
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    val message = getString(R.string.destination_not_empty_message)
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        editTextDestinationCountry.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                editTextDestinationCountry.clearFocus()
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(editTextDestinationCountry.windowToken, 0)
                true
            } else {
                false
            }
        }
    }

    private fun geocodeAddress(address: String, callback: (LatLng?) -> Unit) {
        lifecycleScope.launch(Dispatchers.IO) {
            val geocoder = Geocoder(this@NewRouteActivity, Locale.getDefault())
            try {
                val addresses = geocoder.getFromLocationName(address, 1)
                if (!addresses.isNullOrEmpty()) {
                    val location = addresses[0]
                    withContext(Dispatchers.Main) {
                        callback(LatLng(location.latitude, location.longitude))
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        callback(null)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    callback(null)
                }
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
        editTextDepartureCity: EditText,
        editTextDepartureCountry: EditText,
        editTextDestinationCity: EditText,
        editTextDestinationCountry: EditText,
        editTextDepartureDate: EditText,
        editTextDriver: EditText,
        editTextVehicle: EditText
    ): Boolean {
        val requiredFieldMessage = getString(R.string.field_required)
        val messageDateShouldBeInFuture = getString(R.string.date_should_be_future)
        var isValid = true

        if (editTextDepartureCity.text.toString().isEmpty()) {
            editTextDepartureCity.error = requiredFieldMessage
            isValid = false
        }

        if (editTextDepartureCountry.text.toString().isEmpty()) {
            editTextDepartureCountry.error = requiredFieldMessage
            isValid = false
        }

        if (editTextDestinationCity.text.toString().isEmpty()) {
            editTextDestinationCity.error = requiredFieldMessage
            isValid = false
        }

        if (editTextDestinationCountry.text.toString().isEmpty()) {
            editTextDestinationCountry.error = requiredFieldMessage
            isValid = false
        }

        if (editTextDepartureDate.text.toString().isEmpty()) {
            editTextDepartureDate.error = requiredFieldMessage
            isValid = false
        }

        if (selectedUserId == null) {
            editTextDriver.error = requiredFieldMessage
            isValid = false
        }

        if (selectedVehicleId == null) {
            editTextVehicle.error = requiredFieldMessage
            isValid = false
        }

        if (selectedDate != null && selectedDate!! < Date()) {
            editTextDepartureDate.error = messageDateShouldBeInFuture
            Toast.makeText(this, messageDateShouldBeInFuture, Toast.LENGTH_SHORT).show()
        }

        return isValid
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
