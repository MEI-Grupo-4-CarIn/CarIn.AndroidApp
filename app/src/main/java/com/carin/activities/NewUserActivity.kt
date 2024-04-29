package com.carin.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.carin.R
import java.util.Calendar

class NewUserActivity : AppCompatActivity() {

    private val PICK_IMAGE_REQUEST = 1
    private lateinit var editTextDateOfBirth: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_user)

        val photoUploadImageView = findViewById<ImageView>(R.id.photoUploadImageView)
        photoUploadImageView.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK)
            galleryIntent.type = "image/*"
            startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST)
        }

        editTextDateOfBirth = findViewById(R.id.editTextDateOfBirth)
        editTextDateOfBirth.setOnClickListener {
            showDatePickerDialog()
        }

        val spinnerOptions: Spinner = findViewById(R.id.spinnerOptions)
        val adapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            this,
            R.array.options,
            R.layout.spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerOptions.adapter = adapter

        val iconImageView = findViewById<ImageView>(R.id.iconImageView)
        iconImageView.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            val selectedImageUri = data.data
            val photoUploadImageView = findViewById<ImageView>(R.id.photoUploadImageView)
            photoUploadImageView.setImageURI(selectedImageUri)
            photoUploadImageView.scaleType = ImageView.ScaleType.CENTER_CROP
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this,
            DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                editTextDateOfBirth.setText(selectedDate)
            }, year, month, day)

        datePickerDialog.show()
    }
}

