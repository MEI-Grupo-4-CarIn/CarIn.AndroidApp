package com.carin.activities

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.carin.R

class ConfirmPasswordActivity : AppCompatActivity() {
    private lateinit var relativeLayout: RelativeLayout
    private lateinit var relativeLayout1: RelativeLayout
    private lateinit var buttonLogin: Button
    private lateinit var editTextEmail: EditText
    private lateinit var textViewTimer: TextView
    private lateinit var timer: CountDownTimer
    private var clickCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm)

        relativeLayout = findViewById(R.id.relativeLayout)
        relativeLayout1 = findViewById(R.id.relativeLayout1)
        buttonLogin = findViewById(R.id.buttonLogin)
        editTextEmail = findViewById(R.id.editTextEmail)
        textViewTimer = findViewById(R.id.textViewTimer)

        buttonLogin.setOnClickListener {

            val email = editTextEmail.text.toString().trim()
            if (isValidEmail(email)) {
                relativeLayout.visibility = View.GONE
                relativeLayout1.visibility = View.VISIBLE

                clickCount++

                if (clickCount == 1) {
                    relativeLayout.visibility = View.GONE
                    relativeLayout1.visibility = View.VISIBLE
                    startTimer()
                    setupCodeInput()
                } else if (clickCount == 2) {
                    val intent = Intent(this, RecoverPasswordActivity::class.java)
                    startActivity(intent)
                    finish()
                }

            } else {
                val errorMessage = if (isDeviceInEnglish()) {
                    "Please enter a valid email address!"
                } else {
                    "Por favor, insira um email válido!"
                }
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startTimer() {
        timer = object : CountDownTimer(5 * 60 * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = millisUntilFinished / 1000 / 60
                val seconds = (millisUntilFinished / 1000) % 60
                textViewTimer.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                val intent = Intent(this@ConfirmPasswordActivity, LoginActivity::class.java)
                startActivity(intent)
            }
        }
        timer.start()
    }

    private fun isValidEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }

    private fun setupCodeInput() {
        val editText1 = findViewById<EditText>(R.id.editTextDigit1)
        val editText2 = findViewById<EditText>(R.id.editTextDigit2)
        val editText3 = findViewById<EditText>(R.id.editTextDigit3)
        val editText4 = findViewById<EditText>(R.id.editTextDigit4)
        val editText5 = findViewById<EditText>(R.id.editTextDigit5)
        val editText6 = findViewById<EditText>(R.id.editTextDigit6)

        val editTexts = listOf(editText1, editText2, editText3, editText4, editText5, editText6)

        val editTextsExceptLast = editTexts.subList(0, editTexts.size - 1)

        editTextsExceptLast.forEachIndexed { index, currentEditText ->
            val nextEditText = editTexts[index + 1]

            currentEditText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s != null && s.isNotEmpty()) {
                        if (nextEditText.text.isEmpty()) {
                            nextEditText.requestFocus()
                        } else {
                            val errorMessage = if (isDeviceInEnglish()) {
                                "Please enter a valid email address!"
                            } else {
                                "Por favor, insira um email válido!"
                            }
                            Toast.makeText(this@ConfirmPasswordActivity, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            })
        }
    }
    private fun isDeviceInEnglish(): Boolean {
        val language = resources.configuration.locale.language
        return language == "en"
    }




}
