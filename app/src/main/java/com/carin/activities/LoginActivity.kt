package com.carin.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.carin.BuildConfig
import com.carin.R
import com.carin.data.models.auth.UserAuth
import com.carin.data.models.request.AuthLoginRequest
import com.carin.data.models.response.AuthLoginResponse
import com.carin.data.remote.AuthService
import com.carin.domain.enums.Role
import com.carin.utils.AuthUtils
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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
            val user = AuthLoginResponse(
                "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjMiLCJlbWFpbCI6ImFkbWluQGVtYWlsLmNvbSIsImZpcnN0TmFtZSI6IkFkbWluIiwibGFzdE5hbWUiOiJBZG1pbiIsInJvbGUiOiIxIiwiZXhwIjoxNzE2NTU5ODE4LCJpc3MiOiJBdXRoLk1pY3JvU2VydmljZSIsImF1ZCI6IkF1dGguTWljcm9TZXJ2aWNlIn0.UrQhmJ_taTs_escgDgJloJlbYnxuQWFft_Gn1u8VtTbYHmc_nbQ5ggirE7z3jnPdHxaGG11epMDvKs7ub08BooclgesPmxjFjGStVGtGRqt8Mz8G1oaSGqyxqo4C4z6x4UQMGVL-sWdFW8Hh5w_0NJJD26hNrooEIFQo0dMOIkA7g-bf9e0E1mxX2BJO8IWaLgQSF377ayXvIogPfaGg8o_4BmDtXofCC6YAK_AGTgOJTeP5K-1ubbVSmE-_k0RjvLiLg3rYGvDUKhMYNv9awrNRlk_4g9kN5mj7OEYBzIhZkyEkpJsbniZ0GwIE3ZIIrwhfDfzQiAPrSMm0LhyGTg",
                "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9",
                600
            )

            AuthUtils.saveUserOnSharedPreferences(this@LoginActivity, user)
            navigateToHome()
        } else {
            val retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val authService = retrofit.create(AuthService::class.java)
            val call = authService.login(AuthLoginRequest(email, password))

            call.enqueue(object : Callback<AuthLoginResponse> {
                override fun onResponse(call: Call<AuthLoginResponse>, response: Response<AuthLoginResponse>) {
                    if (response.isSuccessful) {
                        try {
                            val authResponse = response.body()
                            if (authResponse != null) {
                                AuthUtils.saveUserOnSharedPreferences(this@LoginActivity, authResponse)
                                navigateToHome()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(this@LoginActivity, "An error occurred", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@LoginActivity, "Login failed", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<AuthLoginResponse>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, "An error occurred", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun navigateToHome() {
        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun isDeviceInEnglish(): Boolean {
        val language = resources.configuration.locale.language
        return language == "en"
    }
}
