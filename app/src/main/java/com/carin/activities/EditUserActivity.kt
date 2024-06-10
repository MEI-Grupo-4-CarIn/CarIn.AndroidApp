package com.carin.activities

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.carin.R
import com.carin.di.RepositoryModule
import com.carin.domain.models.UserModel
import com.carin.domain.models.UserUpdateModel
import com.carin.utils.AuthUtils
import com.carin.utils.Resource
import com.carin.viewmodels.InfoUserViewModel
import com.carin.viewmodels.InfoUserViewModelFactory
import java.text.SimpleDateFormat
import java.util.Locale

class EditUserActivity : AppCompatActivity() {

    private lateinit var viewModel: InfoUserViewModel
    private lateinit var userModel: UserModel
    private val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user)

        val closeImgView = findViewById<ImageView>(R.id.editUserCloseImgView)
        closeImgView.setOnClickListener {
            finish()
        }

        val firstNameEditText = findViewById<EditText>(R.id.editTextFirstName)
        val lastNameEditText = findViewById<EditText>(R.id.editTextLastName)
        val birthDateEditText = findViewById<EditText>(R.id.editTextDateOfBirth)
        val emailEditText = findViewById<EditText>(R.id.editTextEmail)

        var updatedFirstName: String? = null
        var updatedLastName: String? = null
        var updatedEmail: String? = null

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
                updatedFirstName = if (firstName != userModel.firstName) firstName else null
                updatedLastName = if (lastName != userModel.lastName) lastName else null
                updatedEmail = if (email != userModel.email) email else null

                val userUpdateModel = UserUpdateModel(
                    id = userModel.id,
                    firstName = updatedFirstName,
                    lastName = updatedLastName,
                    email = updatedEmail,
                )

                viewModel.updateUser(userUpdateModel)
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
}