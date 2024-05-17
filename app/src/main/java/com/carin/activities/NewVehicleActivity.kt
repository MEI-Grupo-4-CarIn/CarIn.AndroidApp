package com.carin.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.carin.R
import java.util.Calendar

class NewVehicleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_vehicle)

        val editTextDateRegister = findViewById<EditText>(R.id.editTextDateRegister)
        val editTextDateAcquisition = findViewById<EditText>(R.id.editTextDateAcquisition)
        val iconImageView = findViewById<ImageView>(R.id.iconImageView)

        editTextDateRegister.setOnClickListener {
            showDatePickerDialog(editTextDateRegister)
        }

        editTextDateAcquisition.setOnClickListener {
            showDatePickerDialog(editTextDateAcquisition)
        }

        iconImageView.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.animator.slide_down, 0)
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



