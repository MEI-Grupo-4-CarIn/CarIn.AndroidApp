package com.carin.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.carin.R
import com.google.android.material.materialswitch.MaterialSwitch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class NewSchedulingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_scheduling)

        val iconImageView = findViewById<ImageView>(R.id.iconImageView)
        iconImageView.setOnClickListener {
            finish()
        }

        val switchButton = findViewById<MaterialSwitch>(R.id.switchButton)

        switchButton.setOnClickListener {
            val isChecked = switchButton.isChecked

            switchButton.isChecked = !isChecked
        }

        val editTextTop = findViewById<EditText>(R.id.editTextTop)
        val editTextExpectedArrival = findViewById<EditText>(R.id.editTextExpectedArrival)

        editTextTop.setOnClickListener {
            showDateTimePickerDialog(editTextTop)
        }

        editTextExpectedArrival.setOnClickListener {
            showDateTimePickerDialog(editTextExpectedArrival)
        }
    }

    private fun showDateTimePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                val timePickerDialog = TimePickerDialog(
                    this,
                    TimePickerDialog.OnTimeSetListener { _, selectedHourOfDay, selectedMinute ->
                        val selectedDateTime = Calendar.getInstance()
                        selectedDateTime.set(selectedYear, selectedMonth, selectedDayOfMonth, selectedHourOfDay, selectedMinute)
                        val selectedDateTimeFormatted = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(selectedDateTime.time)
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

}
