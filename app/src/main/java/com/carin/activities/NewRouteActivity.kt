package com.carin.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.carin.R
import java.util.Calendar

class NewRouteActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var startPoint: LatLng? = null
    private var endPoint: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_route)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mapFragment.view?.setOnLongClickListener {
            startPoint = mMap.cameraPosition.target
            val uri = Uri.parse("geo:${startPoint?.latitude},${startPoint?.longitude}")
            val mapIntent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(mapIntent)
            true
        }

        val editTextDateRegister = findViewById<EditText>(R.id.editTextDateRegister)
        val editTextExpectedArrival = findViewById<EditText>(R.id.editTextExpectedArrival)

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
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN

        mMap.setOnMapClickListener { latLng ->
            if (startPoint == null) {
                startPoint = latLng
                mMap.addMarker(MarkerOptions().position(latLng).title("Start Point"))
            } else if (endPoint == null) {
                endPoint = latLng
                mMap.addMarker(MarkerOptions().position(latLng).title("End Point"))
                drawRoute()
            }
        }

        mMap.setOnMapLongClickListener { latLng ->
            startPoint = latLng
            mMap.clear()
            mMap.addMarker(MarkerOptions().position(latLng).title("Start Point"))
        }
    }


    private fun drawRoute() {
        startPoint?.let { start ->
            endPoint?.let { end ->
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
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
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


