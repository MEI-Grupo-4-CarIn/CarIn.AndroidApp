package com.carin.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.carin.R
import java.util.Calendar

class EditRouteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_route)

        val iconImageView = findViewById<ImageView>(R.id.iconImageView)
        iconImageView.setOnClickListener {
            Intent(this, InfoRouteActivity::class.java)
            finish()
        }

        val editTextDateRegister = findViewById<EditText>(R.id.editTextDateRegister)
        val editTextExpectedArrival = findViewById<EditText>(R.id.editTextExpectedArrival)

        editTextDateRegister.setOnClickListener {
            showDatePickerDialog(editTextDateRegister)
        }

        editTextExpectedArrival.setOnClickListener {
            showDatePickerDialog(editTextExpectedArrival)
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

