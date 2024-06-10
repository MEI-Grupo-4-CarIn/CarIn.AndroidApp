package com.carin.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.carin.R
import java.util.Calendar

class EditVehicleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_vehicle)

        val iconImageView: ImageView = findViewById(R.id.iconImageView)

        iconImageView.setOnClickListener {
            Intent(this, InfoVehicleActivity::class.java)
            finish()
        }

        val editTextDateRegister = findViewById<EditText>(R.id.editTextDateRegister)
        val editTextDateAcquisition = findViewById<EditText>(R.id.editTextDateAcquisition)

        editTextDateRegister.setOnClickListener {
            showDatePickerDialog(editTextDateRegister)
        }

        editTextDateAcquisition.setOnClickListener {
            showDatePickerDialog(editTextDateAcquisition)
        }
    }

    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, dayOfMonth ->
                val selectedDate = String.format("%02d-%02d-%04d", dayOfMonth, selectedMonth + 1, selectedYear)
                editText.setText(selectedDate)
            },
            year, month, dayOfMonth
        )

        datePickerDialog.show()
    }
}
