package com.carin.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.carin.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import java.io.IOException
import java.util.Calendar
import java.util.Locale

class NewRouteActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var startPoint: LatLng? = null
    private var endPoint: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_route)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val editTextDateRegister = findViewById<EditText>(R.id.editTextDateRegister)
        val editTextExpectedArrival = findViewById<EditText>(R.id.editTextExpectedArrival)
        val editTextDeparture = findViewById<EditText>(R.id.editTextDeparture)
        val editTextDestination = findViewById<EditText>(R.id.editTextDestination)

        editTextDateRegister.setOnClickListener {
            showDatePicker(editTextDateRegister)
        }

        editTextExpectedArrival.setOnClickListener {
            showDatePicker(editTextExpectedArrival)
        }

        val iconImageView = findViewById<ImageView>(R.id.iconImageView)
        iconImageView.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.animator.slide_down, 0)
        }

        editTextDeparture.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val address = editTextDeparture.text.toString()
                if (address.isNotEmpty()) {
                    startPoint = getLocationFromAddress(address)
                    startPoint?.let {
                        drawRoute()
                    } ?: run {
                        Toast.makeText(this, "Invalid departure address", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Departure address cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
        }

        editTextDestination.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val address = editTextDestination.text.toString()
                if (address.isNotEmpty()) {
                    endPoint = getLocationFromAddress(address)
                    endPoint?.let {
                        drawRoute()
                    } ?: run {
                        Toast.makeText(this, "Invalid destination address", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Destination address cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
        }

        editTextDestination.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                editTextDestination.clearFocus()
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(editTextDestination.windowToken, 0)
                true
            }
            else {
                false
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
    }

    private fun getLocationFromAddress(address: String): LatLng? {
        return try {
            val geocoder = Geocoder(this, Locale.getDefault())
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
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            null
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

    private fun showDatePicker(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                val selectedDate = "$dayOfMonth-${monthOfYear + 1}-$year"
                editText.setText(selectedDate)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }
}
