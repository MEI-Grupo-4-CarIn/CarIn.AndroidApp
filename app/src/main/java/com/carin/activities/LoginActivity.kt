package com.carin.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.carin.R
import com.carin.utils.AuthUtils
import com.carin.utils.Resource
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted
            Toast.makeText(this, getString(R.string.permission_notifications_granted), Toast.LENGTH_SHORT).show()
        } else {
            // Permission denied
            Toast.makeText(this, getString(R.string.permission_notifications_denied), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailEditText = findViewById(R.id.editTextEmail)
        intent.getStringExtra("email")?.let {
            emailEditText.setText(it)
        }

        val buttonRegister: Button = findViewById(R.id.buttonRegister)
        buttonRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        val buttonLogin = findViewById<Button>(R.id.buttonLogin)
        buttonLogin.setOnClickListener {
            val enteredEmail = emailEditText.text.toString()
            val enteredPassword = findViewById<EditText>(R.id.editTextPassword).text.toString()

            if (validateInputs(enteredEmail, enteredPassword)) {
                performLogin(enteredEmail, enteredPassword)
            }
        }

        val textViewForgotPassword = findViewById<TextView>(R.id.textViewForgotPassword)
        textViewForgotPassword.setOnClickListener {
            val intent = Intent(this, ConfirmPasswordActivity::class.java)
            startActivity(intent)
        }

        // Ask for notification permission on Android 13 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            askNotificationPermission()
        }
    }

    private fun performLogin(email: String, password: String) {
        lifecycleScope.launch {
            AuthUtils.authenticateUser(this@LoginActivity, email, password).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        if (result.data == true) {
                            navigateToHome()
                        }
                    }
                    is Resource.Error -> {
                        Toast.makeText(this@LoginActivity, result.message, Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading -> {}
                }
            }
        }
    }

    private fun navigateToHome() {
        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun validateInputs(email: String, password: String): Boolean {
        val emailEditText = findViewById<EditText>(R.id.editTextEmail)
        val passwordEditText = findViewById<EditText>(R.id.editTextPassword)

        var isValid = true

        if (email.isEmpty()) {
            emailEditText.error = "Email is required"
            isValid = false
        }

        if (password.isEmpty()) {
            passwordEditText.error = "Password is required"
            isValid = false
        }

        return isValid
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun askNotificationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Permission granted
            }
            shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                Toast.makeText(this, getString(R.string.notifications_is_required), Toast.LENGTH_LONG).show()
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
