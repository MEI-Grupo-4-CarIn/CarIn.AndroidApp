package com.carin.activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.carin.R
import com.carin.di.RepositoryModule
import com.carin.domain.enums.FuelType
import com.carin.domain.models.VehicleCreationModel
import com.carin.utils.Resource
import com.carin.viewmodels.CreateVehicleViewModel
import com.carin.viewmodels.CreateVehicleViewModelFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class NewVehicleActivity : AppCompatActivity() {

    private lateinit var createVehicleViewModel: CreateVehicleViewModel
    private var selectedFuelType: FuelType? = null
    private var typeFuelStrings: List<String> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_vehicle)

        val vehicleRepository = RepositoryModule.provideVehicleRepository(this)
        val createVehicleFactory = CreateVehicleViewModelFactory(vehicleRepository)
        createVehicleViewModel = ViewModelProvider(this, createVehicleFactory).get(CreateVehicleViewModel::class.java)

        val editTextDateRegister = findViewById<EditText>(R.id.editTextDateRegister)
        val editTextDateAcquisition = findViewById<EditText>(R.id.editTextDateAcquisition)

        editTextDateRegister.setOnClickListener {
            showDatePicker(editTextDateRegister)
        }

        editTextDateAcquisition.setOnClickListener {
            showDatePicker(editTextDateAcquisition)
        }

        val iconImageView = findViewById<ImageView>(R.id.iconImageView)
        iconImageView.setOnClickListener {
            overridePendingTransition(R.animator.slide_down, 0)
            finish()
        }

        val editTextBrand = findViewById<EditText>(R.id.editTextBrand)
        val editTextModel = findViewById<EditText>(R.id.editTextModel)
        val editTextRegistration = findViewById<EditText>(R.id.editTextRegistration)
        val editTextVIN = findViewById<EditText>(R.id.editTextVIN)
        val editTextColor = findViewById<EditText>(R.id.editTextColor)
        val editTextCategory = findViewById<EditText>(R.id.editTextCategory)
        val kms = findViewById<EditText>(R.id.kms)
        val capacity = findViewById<EditText>(R.id.capacity)
        val spinnerFuelType = findViewById<Spinner>(R.id.spinnerTypeFuel)
        val editTextAvgConsumption = findViewById<EditText>(R.id.editTextAvgConsumption)
        val buttonCreateVehicle = findViewById<View>(R.id.buttonCreateVehicle)

        setupTypeFuelSpinner(spinnerFuelType)

        buttonCreateVehicle.setOnClickListener {
            if (validateInputs(
                    editTextBrand,
                    editTextModel,
                    editTextRegistration,
                    editTextVIN,
                    editTextColor,
                    editTextDateRegister,
                    editTextDateAcquisition,
                    editTextCategory,
                    kms,
                    capacity,
                    editTextAvgConsumption)
            ) {

                    val vehicleCreationModel = VehicleCreationModel(
                        brand = editTextBrand.text.toString(),
                        model = editTextModel.text.toString(),
                        licensePlate = editTextRegistration.text.toString(),
                        vin = editTextVIN.text.toString(),
                        color = editTextColor.text.toString(),
                        registerDate = convertBirthDateToCSharpFormat(editTextDateRegister.text.toString()) ?: "",
                        acquisitionDate = convertBirthDateToCSharpFormat(editTextDateAcquisition.text.toString()) ?: "",
                        category = editTextCategory.text.toString(),
                        kms = kms.text.toString().toDoubleOrNull() ?: 0.0,
                        capacity = capacity.text.toString().toDoubleOrNull() ?: 0.0,
                        fuelType = selectedFuelType ?: FuelType.Diesel,
                        averageFuelConsumption = editTextAvgConsumption.text.toString().toDoubleOrNull() ?: 0.0
                    )
                    createVehicleViewModel.createVehicle(vehicleCreationModel)
            }
        }

        createVehicleViewModel.creationState.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    buttonCreateVehicle.isEnabled = false
                }
                is Resource.Success -> {
                    buttonCreateVehicle.isEnabled = true
                    Toast.makeText(this, getString(R.string.vehicle_created_message), Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, InfoVehicleActivity::class.java)
                    intent.putExtra("vehicleId", resource.data)
                    startActivity(intent)
                    finish()
                }
                is Resource.Error -> {
                    buttonCreateVehicle.isEnabled = true
                    Toast.makeText(this, resource.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setupTypeFuelSpinner(spinner: Spinner) {

        typeFuelStrings = FuelType.entries.map {
            getString(resources.getIdentifier(it.stringKey, "string", packageName))
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, typeFuelStrings)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {

                val selectedString = parent.getItemAtPosition(position).toString()

                selectedFuelType = FuelType.entries.find {
                    getString(resources.getIdentifier(it.stringKey, "string", packageName)) == selectedString
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
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
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                val selectedDateTime = Calendar.getInstance()
                selectedDateTime.set(
                    selectedYear,
                    selectedMonth,
                    selectedDayOfMonth
                )
                val selectedDateTimeFormatted =
                    SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(
                        selectedDateTime.time
                    )
                editText.setText(selectedDateTimeFormatted)
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun validateInputs(
        editTextBrand : TextView,
        editTextModel : TextView,
        editTextRegistration : TextView,
        editTextVIN : TextView,
        editTextColor : TextView,
        editTextDateRegister : TextView,
        editTextDateAcquisition : TextView,
        editTextClass : TextView,
        kms : TextView,
        capacity : TextView,
        editTextAvgConsumption : TextView,
    ): Boolean {
        val requiredFieldMessage = getString(R.string.field_required)
        var isValid = true

        if (editTextBrand.text.toString().isEmpty()) {
            editTextBrand.error = requiredFieldMessage
            isValid = false
        }

        if (editTextModel.text.toString().isEmpty()) {
            editTextModel.error = requiredFieldMessage
            isValid = false
        }

        if (editTextRegistration.text.toString().isEmpty()) {
            editTextRegistration.error = requiredFieldMessage
            isValid = false
        }

        if (editTextVIN.text.toString().isEmpty()) {
            editTextVIN.error = requiredFieldMessage
            isValid = false
        }

        if (editTextColor.text.toString().isEmpty()) {
            editTextColor.error = requiredFieldMessage
            isValid = false
        }

        if (editTextDateRegister.text.toString().isEmpty()) {
            editTextDateRegister.error = requiredFieldMessage
            isValid = false
        }

        if (editTextDateAcquisition.text.toString().isEmpty()) {
            editTextDateAcquisition.error = requiredFieldMessage
            isValid = false
        }

        if (editTextClass.text.toString().isEmpty()) {
            editTextClass.error = requiredFieldMessage
            isValid = false
        }

        if (kms.text.toString().isEmpty()) {
            kms.error = requiredFieldMessage
            isValid = false
        }

        if (capacity.text.toString().isEmpty()) {
            capacity.error = requiredFieldMessage
            isValid = false
        }

        if (editTextAvgConsumption.text.toString().isEmpty()) {
            editTextAvgConsumption.error = requiredFieldMessage
            isValid = false
        }

        return isValid
    }

    private fun convertBirthDateToCSharpFormat(birthDate: String): String? {
        val inputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val date: Date? = inputFormat.parse(birthDate)
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return date?.let { outputFormat.format(it) }
    }
}



