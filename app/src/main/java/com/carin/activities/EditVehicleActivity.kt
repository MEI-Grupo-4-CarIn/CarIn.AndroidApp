package com.carin.activities

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.carin.R
import com.carin.di.RepositoryModule
import com.carin.domain.enums.VehicleStatus
import com.carin.domain.models.VehicleModel
import com.carin.domain.models.VehicleUpdateModel
import com.carin.utils.NetworkUtils
import com.carin.utils.Resource
import com.carin.viewmodels.InfoVehicleViewModel
import com.carin.viewmodels.InfoVehicleViewModelFactory
import java.text.SimpleDateFormat
import java.util.Locale

class EditVehicleActivity : AppCompatActivity() {

    private lateinit var infoVehicleViewModel: InfoVehicleViewModel
    private lateinit var vehicleModel: VehicleModel
    private val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private var selectedVehicleStatus: VehicleStatus? = null
    private var vehicleStatusStrings: List<String> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_vehicle)

        val routeRepository = RepositoryModule.provideRouteRepository(this)
        val vehicleRepository = RepositoryModule.provideVehicleRepository(this)

        val infoVehicleFactory = InfoVehicleViewModelFactory(vehicleRepository, routeRepository)
        infoVehicleViewModel = ViewModelProvider(this, infoVehicleFactory)[InfoVehicleViewModel::class.java]

        val closeImgView = findViewById<ImageView>(R.id.closeImgView)
        val editTextBrand = findViewById<EditText>(R.id.editTextBrand)
        val editTextModel = findViewById<EditText>(R.id.editTextModel)
        val editTextLicencePlate = findViewById<EditText>(R.id.editTextRegistration)
        val editTextVIN = findViewById<EditText>(R.id.editTextVIN)
        val editTextColor = findViewById<EditText>(R.id.editTextColor)
        val editTextDateRegister = findViewById<EditText>(R.id.editTextDateRegister)
        val editTextDateAcquisition = findViewById<EditText>(R.id.editTextDateAcquisition)
        val editTextCategory = findViewById<EditText>(R.id.editTextClass)
        val editTextKms = findViewById<EditText>(R.id.kms)
        val editTextCapacity = findViewById<EditText>(R.id.capacity)
        val editTextTypeFuel = findViewById<EditText>(R.id.editTextTypeFuel)
        val editTextAvgConsumption = findViewById<EditText>(R.id.editTextAvgConsumption)
        val spinnerOptionsVehicleStatus = findViewById<Spinner>(R.id.spinnerOptionsVehicleStatus)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val errorTextView = findViewById<TextView>(R.id.errorTextView)
        val buttonSaveVehicle = findViewById<Button>(R.id.buttonSaveVehicle)

        closeImgView.setOnClickListener {
            finish()
        }

        setupVehicleStatusSpinner(spinnerOptionsVehicleStatus)

        infoVehicleViewModel.uiDetailsState.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    errorTextView.visibility = View.GONE
                }
                is Resource.Success -> {
                    resource.data?.let { vehicle ->
                        vehicleModel = vehicle

                        editTextBrand.setText(vehicle.brand)
                        editTextModel.setText(vehicle.model)
                        editTextLicencePlate.setText(vehicle.licensePlate)
                        editTextVIN.setText(vehicle.vin)
                        editTextColor.setText(vehicle.color)
                        editTextDateRegister.setText(formatter.format(vehicle.registerDate))
                        editTextDateAcquisition.setText(formatter.format(vehicle.acquisitionDate))
                        editTextCategory.setText(vehicle.category)
                        editTextKms.setText(vehicle.kms.toString())
                        editTextCapacity.setText(vehicle.capacity.toString())
                        editTextTypeFuel.setText(vehicle.fuelType.toString())
                        editTextAvgConsumption.setText(vehicle.averageFuelConsumption.toString())

                        val initialStatus = vehicle.status
                        val initialPosition = vehicleStatusStrings.indexOf(
                            getString(resources.getIdentifier(initialStatus.stringKey, "string", packageName))
                        )
                        if (initialPosition >= 0) {
                            spinnerOptionsVehicleStatus.setSelection(initialPosition)
                        }
                    }
                    progressBar.visibility = View.GONE
                }
                is Resource.Error -> {
                    progressBar.visibility = View.GONE
                    errorTextView.visibility = View.VISIBLE
                }
            }
        }

        infoVehicleViewModel.loadVehicleDetails(intent.getStringExtra("vehicleId") ?: "")

        buttonSaveVehicle.setOnClickListener {
            val isNetworkAvailable = NetworkUtils.isNetworkAvailable(this)
            if (!isNetworkAvailable) {
                errorTextView.visibility = View.VISIBLE
            } else {
                errorTextView.visibility = View.GONE
            }

            val selectedColor = editTextColor.text.toString()
            val selectedKms = editTextKms.text.toString().toDoubleOrNull()
            val selectedAverageFuelConsumption = editTextAvgConsumption.text.toString().toDoubleOrNull()

            if (validateInputs(editTextColor, editTextKms, editTextAvgConsumption) && isNetworkAvailable) {
                val updatedColor = if (selectedColor != vehicleModel.color) selectedColor else null
                val updatedKms = if (selectedKms != vehicleModel.kms) selectedKms else null
                val updatedAverageFuelConsumption = if (selectedAverageFuelConsumption != vehicleModel.averageFuelConsumption) selectedAverageFuelConsumption else null
                val updatedVehicleStatus = if (selectedVehicleStatus != vehicleModel.status) selectedVehicleStatus else null

                if (hasChanges(updatedColor, updatedKms, updatedAverageFuelConsumption, updatedVehicleStatus)) {
                    val vehicleUpdateModel = VehicleUpdateModel(
                        id = vehicleModel.id,
                        color = updatedColor,
                        kms = updatedKms,
                        averageFuelConsumption = updatedAverageFuelConsumption,
                        status = updatedVehicleStatus
                    )

                    infoVehicleViewModel.updateVehicle(vehicleUpdateModel)
                }
            }

        }

        infoVehicleViewModel.vehicleUpdateState.observe(this) { result ->
            when (result) {
                is Resource.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    errorTextView.visibility = View.GONE
                }
                is Resource.Success -> {
                    progressBar.visibility = View.GONE
                    errorTextView.visibility = View.GONE

                    val successMessage = getString(R.string.vehicle_updated_success)
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

    private fun setupVehicleStatusSpinner(spinner: Spinner) {
        vehicleStatusStrings = VehicleStatus.entries.map {
            getString(resources.getIdentifier(it.stringKey, "string", packageName))
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, vehicleStatusStrings)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // Get the selected string
                val selectedString = parent.getItemAtPosition(position).toString()

                // Find the corresponding VehicleStatus enum value
                selectedVehicleStatus = VehicleStatus.entries.find {
                    getString(resources.getIdentifier(it.stringKey, "string", packageName)) == selectedString
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }

    private fun validateInputs(
        editTextColor : TextView,
        kms : TextView,
        editTextAvgConsumption : TextView,
    ): Boolean {
        val requiredFieldMessage = getString(R.string.field_required)
        var isValid = true

        if (editTextColor.text.toString().isEmpty()) {
            editTextColor.error = requiredFieldMessage
            isValid = false
        }

        if (kms.text.toString().isEmpty()) {
            kms.error = requiredFieldMessage
            isValid = false
        }

        if (editTextAvgConsumption.text.toString().isEmpty()) {
            editTextAvgConsumption.error = requiredFieldMessage
            isValid = false
        }

        return isValid
    }

    private fun hasChanges(
        updatedColor: String?,
        updatedKms: Double?,
        updatedAverageFuelConsumption: Double?,
        updatedVehicleStatus: VehicleStatus?,
    ): Boolean {
        return  updatedColor != null ||
                updatedKms != null ||
                updatedAverageFuelConsumption != null ||
                updatedVehicleStatus != null
    }

}
