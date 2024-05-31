package com.carin.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.carin.R
import com.carin.data.remote.dto.auth.AuthTokenDto
import com.carin.utils.AuthUtils
import com.carin.utils.Resource
import com.carin.workers.RefreshTokenWorker
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {

    private val defaultEmail = "teste@example.com"
    private val defaultPassword = "senha123"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val buttonRegister: Button = findViewById(R.id.buttonRegister)

        buttonRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        val buttonLogin = findViewById<Button>(R.id.buttonLogin)
        buttonLogin.setOnClickListener {
            val enteredEmail = findViewById<EditText>(R.id.editTextEmail).text.toString()
            val enteredPassword = findViewById<EditText>(R.id.editTextPassword).text.toString()

            performLogin(enteredEmail, enteredPassword)
        }

        val textViewForgotPassword = findViewById<TextView>(R.id.textViewForgotPassword)
        textViewForgotPassword.setOnClickListener {
            val intent = Intent(this, ConfirmPasswordActivity::class.java)
            startActivity(intent)
        }
    }

    private fun performLogin(email: String, password: String) {

        if (email == defaultEmail && password == defaultPassword) {
            val user = AuthTokenDto(
                "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjMiLCJlbWFpbCI6ImFkbWluQGVtYWlsLmNvbSIsImZpcnN0TmFtZSI6IkFkbWluIiwibGFzdE5hbWUiOiJBZG1pbiIsInJvbGUiOiIxIiwiZXhwIjoxNzE2NTU5ODE4LCJpc3MiOiJBdXRoLk1pY3JvU2VydmljZSIsImF1ZCI6IkF1dGguTWljcm9TZXJ2aWNlIn0.UrQhmJ_taTs_escgDgJloJlbYnxuQWFft_Gn1u8VtTbYHmc_nbQ5ggirE7z3jnPdHxaGG11epMDvKs7ub08BooclgesPmxjFjGStVGtGRqt8Mz8G1oaSGqyxqo4C4z6x4UQMGVL-sWdFW8Hh5w_0NJJD26hNrooEIFQo0dMOIkA7g-bf9e0E1mxX2BJO8IWaLgQSF377ayXvIogPfaGg8o_4BmDtXofCC6YAK_AGTgOJTeP5K-1ubbVSmE-_k0RjvLiLg3rYGvDUKhMYNv9awrNRlk_4g9kN5mj7OEYBzIhZkyEkpJsbniZ0GwIE3ZIIrwhfDfzQiAPrSMm0LhyGTg",
                "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9",
                600
            )

            AuthUtils.saveUserOnSharedPreferences(this@LoginActivity, user)
            navigateToHome()
        } else {
            lifecycleScope.launch {
                AuthUtils.authenticateUser(this@LoginActivity, email, password).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            if (result.data == true) {
                                navigateToHome()
                                scheduleTokenRefreshWorker(this@LoginActivity)
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
    }

    private fun navigateToHome() {
        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun scheduleTokenRefreshWorker(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val refreshTokenWorkRequest = PeriodicWorkRequestBuilder<RefreshTokenWorker>(6, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "RefreshTokenWorker",
            ExistingPeriodicWorkPolicy.UPDATE,
            refreshTokenWorkRequest
        )
    }
}
