package com.carin.activities

import android.content.Intent
import android.location.Geocoder
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
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.carin.R
import com.carin.di.RepositoryModule
import com.carin.domain.enums.Role
import com.carin.domain.enums.RouteStatus
import com.carin.utils.AuthUtils
import com.carin.utils.Resource
import com.carin.utils.getStringResourceByName
import com.carin.viewmodels.InfoRouteViewModel
import com.carin.viewmodels.InfoRouteViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.random.Random

class InfoRouteActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var viewModel: InfoRouteViewModel
    private lateinit var routeId: String
    private var startPoint: LatLng? = null
    private var endPoint: LatLng? = null
    private val birthDateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    private val decimalFormat = DecimalFormat("#.##")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_route)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val userAuth = AuthUtils.getUserAuth(this)
        userAuth?.let {
            adjustUIBasedOnRole(it.role)
            if (it.role == Role.Admin || it.role == Role.Manager) {
                prepareOptionsMenu()
            }
        }

        val routeIdFromIntent = intent.getStringExtra("routeId") ?: ""
        routeId = routeIdFromIntent

        val infoRouteGoBackIcon = findViewById<ImageView>(R.id.infoRouteGoBackIcon)
        infoRouteGoBackIcon.setOnClickListener {
            finish()
        }

        val infoRouteDetailsDepartureTxt = findViewById<TextView>(R.id.infoRouteDetailsDepartureTxt)
        val infoRouteDetailsDestinationTxt = findViewById<TextView>(R.id.infoRouteDetailsDestinationTxt)
        val infoRouteDetailsDriverTxt = findViewById<TextView>(R.id.infoRouteDetailsDriverTxt)
        val infoRouteDetailsVehicleTxt = findViewById<TextView>(R.id.infoRouteDetailsVehicleTxt)
        val infoRouteKmsTxt = findViewById<TextView>(R.id.infoRouteKmsTxt)
        val infoRouteDurationTxt = findViewById<TextView>(R.id.infoRouteDurationTxt)
        val infoRouteDetailStatusTxt = findViewById<TextView>(R.id.infoRouteDetailStatusTxt)
        val infoRouteDetailsDepartureDateTxt = findViewById<TextView>(R.id.infoRouteDetailsDepartureDateTxt)
        val infoRouteDetailsBrandTxt = findViewById<TextView>(R.id.infoRouteDetailsBrandTxt)
        val infoRouteDetailsModelTxt = findViewById<TextView>(R.id.infoRouteDetailsModelTxt)
        val infoRouteDetailsFuelTypeTxt = findViewById<TextView>(R.id.infoRouteDetailsFuelTypeTxt)
        val infoRouteDetailsLicensePlateTxt = findViewById<TextView>(R.id.infoRouteDetailsLicensePlateTxt)
        val infoRouteDetailsNameTxt = findViewById<TextView>(R.id.infoRouteDetailsNameTxt)
        val infoRouteDetailsBirthDateTxt = findViewById<TextView>(R.id.infoRouteDetailsBirthDateTxt)
        val infoRouteDetailsAccidentsTxt = findViewById<TextView>(R.id.infoRouteDetailsAccidentsTxt)
        val infoRouteDetailsAgeTxt = findViewById<TextView>(R.id.infoRouteDetailsAgeTxt)
        val infoRouteDetailsEmailTxt = findViewById<TextView>(R.id.infoRouteDetailsEmailTxt)
        val infoRouteDetailsFuelCostsTxt = findViewById<TextView>(R.id.infoRouteDetailsFuelCostsTxt)
        val infoRouteDetailsTollsCostsTxt = findViewById<TextView>(R.id.infoRouteDetailsTollsCostsTxt)
        val infoRouteDetailsOtherCostsTxt = findViewById<TextView>(R.id.infoRouteDetailsOtherCostsTxt)
        val progressBar = findViewById<View>(R.id.progressBar)
        val errorTextView = findViewById<TextView>(R.id.errorTextView)

        val routeRepository = RepositoryModule.provideRouteRepository(this)
        val factory = InfoRouteViewModelFactory(routeRepository)
        viewModel = ViewModelProvider(this, factory)[InfoRouteViewModel::class.java]
        viewModel.uiDetailsState.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    errorTextView.visibility = View.GONE
                }
                is Resource.Success -> {
                    errorTextView.visibility = View.GONE

                    resource.data?.let { route ->
                        infoRouteDetailsDepartureTxt.text = "${route.startPoint.city}, ${route.startPoint.country}"
                        infoRouteDetailsDestinationTxt.text = "${route.endPoint.city}, ${route.endPoint.country}"
                        infoRouteDetailsDriverTxt.text = "${route.user?.firstName} ${route.user?.lastName}"
                        infoRouteDetailsVehicleTxt.text = "${route.vehicle?.brand} ${route.vehicle?.model}"
                        infoRouteKmsTxt.text = "${decimalFormat.format(route.distance)} km"
                        infoRouteDurationTxt.text = "${route.duration} h"
                        infoRouteDetailsDepartureDateTxt.text = formatter.format(route.startDate)
                        infoRouteDetailsBrandTxt.text = route.vehicle?.brand
                        infoRouteDetailsModelTxt.text = route.vehicle?.model
                        infoRouteDetailsFuelTypeTxt.text = route.vehicle?.fuelType?.let {
                            this.getStringResourceByName(it.stringKey)
                        }
                        infoRouteDetailsLicensePlateTxt.text = route.vehicle?.licensePlate
                        infoRouteDetailsNameTxt.text = "${route.user?.firstName} ${route.user?.lastName}"
                        infoRouteDetailsBirthDateTxt.text = birthDateFormatter.format(route.user?.birthDate)
                        infoRouteDetailsAccidentsTxt.text = "0"
                        infoRouteDetailsAgeTxt.text = route.user?.age?.toString() ?: "N/A"
                        infoRouteDetailsEmailTxt.text = route.user?.email
                        infoRouteDetailsFuelCostsTxt.text = route.distance?.times(1.60)?.let { "%.2f €".format(it) } ?: "N/A"
                        infoRouteDetailsTollsCostsTxt.text = if (!route.avoidTolls) {
                            route.distance?.times(0.20)?.let { "%.2f €".format(it) } ?: "0 €"
                        } else {
                            "N/A"
                        }
                        val randomOtherCost = Random.nextDouble(1.00, 100.00)
                        infoRouteDetailsOtherCostsTxt.text = "%.2f €".format(randomOtherCost)

                        prepareStatusTextView(infoRouteDetailStatusTxt, route.status)
                    }

                    setUpMapWithLocations()
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

        viewModel.loadRouteDetails(routeId)
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
                        val intent = Intent(this, EditRouteActivity::class.java)
                        intent.putExtra("routeId", routeId)
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

    override fun onResume() {
        super.onResume()

        routeId.let { viewModel.loadRouteDetails(it) }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
    }

    private fun setUpMapWithLocations() {
        val infoRouteDetailsDepartureTxt = findViewById<TextView>(R.id.infoRouteDetailsDepartureTxt).text.toString()
        val infoRouteDetailsDestinationTxt = findViewById<TextView>(R.id.infoRouteDetailsDestinationTxt).text.toString()

        lifecycleScope.launch {
            val startPointDeferred = async { geocodeAddress(infoRouteDetailsDepartureTxt) }
            val endPointDeferred = async { geocodeAddress(infoRouteDetailsDestinationTxt) }

            val results = awaitAll(startPointDeferred, endPointDeferred)

            startPoint = results[0]
            endPoint = results[1]

            drawRoute()
        }
    }

    private suspend fun geocodeAddress(address: String): LatLng? {
        return withContext(Dispatchers.IO) {
            val geocoder = Geocoder(this@InfoRouteActivity, Locale.getDefault())
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

    private fun prepareStatusTextView(textView: TextView, status: RouteStatus) {
        textView.text = this.getStringResourceByName(status.stringKey)
        when (status) {
            RouteStatus.Pending -> {
                textView.setTextColor(ContextCompat.getColor(this, R.color.colorPending))
            }
            RouteStatus.InProgress -> {
                textView.setTextColor(ContextCompat.getColor(this, R.color.colorInProgress))
            }
            RouteStatus.Completed -> {
                textView.setTextColor(ContextCompat.getColor(this, R.color.colorCompleted))
            }
            RouteStatus.Cancelled -> {
                textView.setTextColor(ContextCompat.getColor(this, R.color.colorCancelled))
            }
        }
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

    private fun showDeleteConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.delete_confirmation, null)
        builder.setView(dialogView)

        val dialog = builder.create()
        dialog.show()

        val btnYes = dialogView.findViewById<Button>(R.id.btnYes)
        btnYes.setOnClickListener {
            inactivateRoute(routeId)
            dialog.dismiss()
        }

        val btnNo = dialogView.findViewById<Button>(R.id.btnNo)
        btnNo.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun inactivateRoute(id: String) {

        viewModel.uiRouteDeleteState.observe(this) { result ->
            when (result) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    if (result.data == true) {
                        Toast.makeText(this@InfoRouteActivity, getString(R.string.route_successfully_deleted), Toast.LENGTH_SHORT).show()

                        finish()
                    }
                }
                is Resource.Error -> {
                    Toast.makeText(this@InfoRouteActivity, result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        viewModel.deleteRoute(routeId)
    }
}


