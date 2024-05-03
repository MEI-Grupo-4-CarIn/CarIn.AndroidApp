package com.carin.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.carin.R

class LoginActivity : AppCompatActivity() {

    private val email = "teste@example.com"
    private val password = "senha123"

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

            if (enteredEmail == email && enteredPassword == password) {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val errorMessage = if (isDeviceInEnglish()) {
                    "Incorrect email or password!"
                } else {
                    "Email ou senha incorretos!"
                }
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
        val textViewForgotPassword = findViewById<TextView>(R.id.textViewForgotPassword)
        textViewForgotPassword.setOnClickListener {
            val intent = Intent(this, ConfirmPasswordActivity::class.java)
            startActivity(intent)
        }
    }

    private fun isDeviceInEnglish(): Boolean {
        val language = resources.configuration.locale.language
        return language == "en"
    }
}
