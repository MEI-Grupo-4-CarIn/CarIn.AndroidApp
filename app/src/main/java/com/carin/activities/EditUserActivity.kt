package com.carin.activities

import android.app.AlertDialog
import android.content.Context
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
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.carin.R
import com.carin.di.RepositoryModule
import com.carin.domain.models.UserAuth
import com.carin.domain.models.UserModel
import com.carin.domain.models.UserUpdateModel
import com.carin.utils.AuthUtils
import com.carin.utils.Resource
import com.carin.viewmodels.InfoUserViewModel
import com.carin.viewmodels.InfoUserViewModelFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale

class EditUserActivity : AppCompatActivity() {

    private val PICK_IMAGE_REQUEST = 1
    private val CAMERA_REQUEST = 2
    private lateinit var viewModel: InfoUserViewModel
    private lateinit var userModel: UserModel
    private val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private var selectedImageUri: Uri? = null
    private var userAuth: UserAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user)

        val closeImgView = findViewById<ImageView>(R.id.editUserCloseImgView)
        closeImgView.setOnClickListener {
            finish()
        }

        val photoUploadImageView = findViewById<ImageView>(R.id.photoUploadImageView)
        photoUploadImageView.setOnClickListener {
            showImageDialog()
        }

        val firstNameEditText = findViewById<EditText>(R.id.editTextFirstName)
        val lastNameEditText = findViewById<EditText>(R.id.editTextLastName)
        val birthDateEditText = findViewById<EditText>(R.id.editTextDateOfBirth)
        val emailEditText = findViewById<EditText>(R.id.editTextEmail)

        val updatedFirstName: String? = null
        val updatedLastName: String? = null
        val updatedEmail: String? = null

        val userRepository = RepositoryModule.provideUserRepository(this)
        val routeRepository = RepositoryModule.provideRouteRepository(this)
        val factory = InfoUserViewModelFactory(userRepository, routeRepository)
        viewModel = ViewModelProvider(this, factory)[InfoUserViewModel::class.java]
        viewModel.uiDetailsState.observe(this) { resource ->
            val progressBar = findViewById<ProgressBar>(R.id.progressBar)
            val errorTextView = findViewById<TextView>(R.id.errorTextView)

            when (resource) {
                is Resource.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    errorTextView.visibility = View.GONE
                }
                is Resource.Success -> {
                    progressBar.visibility = View.GONE
                    errorTextView.visibility = View.GONE

                    resource.data?.let { user ->
                        userModel = user
                        firstNameEditText.setText(user.firstName)
                        lastNameEditText.setText(user.lastName)
                        birthDateEditText.setText(formatter.format(user.birthDate))
                        emailEditText.setText(user.email)
                        setImageForUser(photoUploadImageView, user.id)
                    }
                }
                is Resource.Error -> {
                    progressBar.visibility = View.GONE
                    errorTextView.visibility = View.VISIBLE
                }
            }
        }

        viewModel.loadUserDetails(intent.getIntExtra("userId", -1))

        val saveButton = findViewById<Button>(R.id.editUserSaveButton)
        saveButton.setOnClickListener {
            val firstName = firstNameEditText.text.toString()
            val lastName = lastNameEditText.text.toString()
            val email = emailEditText.text.toString()

            if (validateInputs(firstName, lastName, email)) {
                saveUser(firstName, lastName, email)
            }
        }

        viewModel.userUpdateState.observe(this) { result ->
            val progressBar = findViewById<ProgressBar>(R.id.progressBar)
            val errorTextView = findViewById<TextView>(R.id.errorTextView)

            when (result) {
                is Resource.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    errorTextView.visibility = View.GONE
                }
                is Resource.Success -> {
                    progressBar.visibility = View.GONE
                    errorTextView.visibility = View.GONE

                    val successMessage = getString(R.string.user_update_successful)
                    Toast.makeText(this, successMessage, Toast.LENGTH_SHORT).show()
                    AuthUtils.updateUserAuth(this, updatedFirstName, updatedLastName, updatedEmail)
                    finish()
                }
                is Resource.Error -> {
                    progressBar.visibility = View.GONE
                    errorTextView.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun saveUser(firstName: String, lastName: String, email: String) {

        val userUpdateModel = UserUpdateModel(
            id = userModel.id,
            firstName = firstName,
            lastName = lastName,
            email = email,
        )

        viewModel.updateUser(userUpdateModel)
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): String? {
        val directory = getDir("profile_pics", MODE_PRIVATE)
        userAuth = AuthUtils.getUserAuth(this)
        val fileName = "${userAuth?.userId}.png"
        val file = File(directory, fileName)
        return try {
            FileOutputStream(file).use { fos ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            }
            file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun validateInputs(firstName: String, lastName: String, email: String): Boolean {
        var isValid = true
        val requiredFieldMessage = getString(R.string.field_required)

        if (firstName.isEmpty()) {
            findViewById<EditText>(R.id.editTextFirstName).error = requiredFieldMessage
            isValid = false
        }

        if (lastName.isEmpty()) {
            findViewById<EditText>(R.id.editTextLastName).error = requiredFieldMessage
            isValid = false
        }

        if (email.isEmpty()) {
            findViewById<EditText>(R.id.editTextEmail).error = requiredFieldMessage
            isValid = false
        }

        return isValid
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.data
            displaySelectedImage(selectedImageUri)
        } else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK && data != null) {
            val imageBitmap = data.extras?.get("data") as? Bitmap
            imageBitmap?.let {
                selectedImageUri = getImageUriFromBitmap(it)
                displaySelectedImage(selectedImageUri)
            }
        }
    }

    private fun showImageDialog() {
        val items = arrayOf<CharSequence>(getString(R.string.gallery), getString(R.string.camera))
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.chooseTheOption))
        builder.setItems(items) { _, which ->
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
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.CAMERA),
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
            val scale = 2
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

    private fun setImageForUser(imageView: ImageView, userId: Int) {
        val profilePicDirectory = getDir("profile_pics", Context.MODE_PRIVATE)
        val imagePath = File(profilePicDirectory, "$userId.png").absolutePath

        if (File(imagePath).exists()) {
            val bitmap = BitmapFactory.decodeFile(imagePath)
            imageView.setImageBitmap(getCircularBitmap(bitmap))
        }
    }
    private fun getCircularBitmap(bitmap: Bitmap): Bitmap {
        val size = Math.max(bitmap.width, bitmap.height)
        val scale = 2
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, size * scale, size * scale, true)
        val circleBitmap = Bitmap.createBitmap(size * scale, size * scale, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(circleBitmap)
        val paint = Paint().apply {
            isAntiAlias = true
            shader = BitmapShader(scaledBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        }
        val radius = (size * scale / 2).toFloat()
        canvas.drawCircle(radius, radius, radius, paint)
        return circleBitmap
    }
}