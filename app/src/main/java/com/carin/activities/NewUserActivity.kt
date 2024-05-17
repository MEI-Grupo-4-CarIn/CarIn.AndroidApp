package com.carin.activities

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Shader
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.carin.R
import java.io.ByteArrayOutputStream
import java.util.Calendar

class NewUserActivity : AppCompatActivity() {

    private val PICK_IMAGE_REQUEST = 1
    private val CAMERA_REQUEST = 2
    private lateinit var editTextDateOfBirth: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_user)

        val photoUploadImageView = findViewById<ImageView>(R.id.photoUploadImageView)
        photoUploadImageView.setOnClickListener {
            showImageDialog()
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
            overridePendingTransition(R.animator.slide_down, 0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            val selectedImageUri = data.data
            displaySelectedImage(selectedImageUri)
        } else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK && data != null) {
            val imageBitmap = data.extras?.get("data") as? Bitmap
            imageBitmap?.let {
                val selectedImageUri = getImageUriFromBitmap(it)
                displaySelectedImage(selectedImageUri)
            }
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

    private fun showImageDialog() {
        val items = arrayOf<CharSequence>("${getString(R.string.gallery)}", "${getString(R.string.camera)}")
        val builder = AlertDialog.Builder(this)
        builder.setTitle(" ${getString(R.string.chooseTheOption)}")
        builder.setItems(items) { dialog, which ->
            when (which) {
                0 -> {
                    openGallery()
                }
                1 -> {
                    openCamera()
                }
            }
        }
        builder.show()
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST)
    }

    private fun openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_REQUEST
            )
        } else {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, CAMERA_REQUEST)
        }
    }

    private fun displaySelectedImage(selectedImageUri: Uri?) {
        val photoUploadImageView = findViewById<ImageView>(R.id.photoUploadImageView)
        selectedImageUri?.let {
            val bitmap = getBitmapFromUri(selectedImageUri)
            val size = Math.max(bitmap.width, bitmap.height)
            val scale = 2 // Fator de escala, ajuste conforme necessário
            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, size * scale, size * scale, true)
            val circleBitmap = Bitmap.createBitmap(size * scale, size * scale, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(circleBitmap)
            val paint = Paint().apply {
                isAntiAlias = true
                shader = BitmapShader(scaledBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            }
            val radius = (size * scale / 2).toFloat()
            canvas.drawCircle(radius, radius, radius, paint)
            photoUploadImageView.setImageBitmap(circleBitmap)
        }
    }

    private fun getImageUriFromBitmap(bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            contentResolver,
            bitmap,
            "Title",
            null
        )
        return Uri.parse(path)
    }

    private fun getBitmapFromUri(uri: Uri): Bitmap {
        val inputStream = contentResolver.openInputStream(uri)
        return BitmapFactory.decodeStream(inputStream)
    }

}
