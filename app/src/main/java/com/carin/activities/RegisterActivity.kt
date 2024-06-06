package com.carin.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.carin.R
import com.carin.di.RepositoryModule
import com.carin.domain.models.UserRegisterModel
import com.carin.utils.Resource
import com.carin.viewmodels.RegisterViewModel
import com.carin.viewmodels.RegisterViewModelFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class RegisterActivity : AppCompatActivity() {

    private lateinit var viewModel: RegisterViewModel
    private lateinit var userEmail: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val userRepository = RepositoryModule.provideUserRepository(this)
        val factory = RegisterViewModelFactory(userRepository)
        viewModel = ViewModelProvider(this, factory)[RegisterViewModel::class.java]

        val birthdayEditText = findViewById<EditText>(R.id.editTextBirthday)
        birthdayEditText.setOnClickListener {
            showDatePicker(birthdayEditText)
        }

        val registerButton = findViewById<Button>(R.id.buttonRegister)
        registerButton.setOnClickListener {
            val firstName = findViewById<EditText>(R.id.editTextFirstName).text.toString()
            val lastName = findViewById<EditText>(R.id.editTextLastName).text.toString()
            val birthDate = birthdayEditText.text.toString()
            val email = findViewById<EditText>(R.id.editTextEmail).text.toString()
            val password = findViewById<EditText>(R.id.editTextPassword).text.toString()
            val confirmPassword = findViewById<EditText>(R.id.editTextConfirmPassword).text.toString()

            if (validateInputs(firstName, lastName, birthDate, email, password, confirmPassword)) {
                userEmail = email

                val userRegisterModel = UserRegisterModel(
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    password = password,
                    birthDate = convertBirthDateToCSharpFormat(birthDate) ?: "",
                )

                viewModel.registerUser(userRegisterModel)
            }
        }

        viewModel.registrationState.observe(this) { result ->
            val progressBar = findViewById<ProgressBar>(R.id.progressBar)
            when (result) {
                is Resource.Loading -> {
                    progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, "@string/register_success", Toast.LENGTH_SHORT).show()
                    navigateToLogin()
                }
                is Resource.Error -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                }
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
            { _, year, monthOfYear, dayOfMonth ->
                val selectedDate = "$dayOfMonth-${monthOfYear + 1}-$year"
                editText.setText(selectedDate)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.putExtra("email", userEmail)
        startActivity(intent)
        finish()
    }

    private fun convertBirthDateToCSharpFormat(birthDate: String): String? {
        val inputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val date: Date? = inputFormat.parse(birthDate)
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return date?.let { outputFormat.format(it) }
    }

    private fun validateInputs(firstName: String, lastName: String, birthDate: String?, email: String, password: String, confirmPassword: String): Boolean {
        val birthdayEditText = findViewById<EditText>(R.id.editTextBirthday)
        val emailEditText = findViewById<EditText>(R.id.editTextEmail)
        val passwordEditText = findViewById<EditText>(R.id.editTextPassword)
        val confirmPasswordEditText = findViewById<EditText>(R.id.editTextConfirmPassword)

        var isValid = true

        if (firstName.isEmpty()) {
            findViewById<EditText>(R.id.editTextFirstName).error = "First name is required"
            isValid = false
        }

        if (lastName.isEmpty()) {
            findViewById<EditText>(R.id.editTextLastName).error = "Last name is required"
            isValid = false
        }

        if (birthDate.isNullOrEmpty()) {
            birthdayEditText.error = "Birth date is required"
            isValid = false
        }

        if (email.isEmpty()) {
            emailEditText.error = "Email is required"
            isValid = false
        }

        if (password.isEmpty()) {
            passwordEditText.error = "Password is required"
            isValid = false
        }

        if (confirmPassword.isEmpty()) {
            confirmPasswordEditText.error = "Confirm password is required"
            isValid = false
        }

        if (password != confirmPassword) {
            confirmPasswordEditText.error = "Passwords do not match"
            isValid = false
        }

        return isValid
    }
}
