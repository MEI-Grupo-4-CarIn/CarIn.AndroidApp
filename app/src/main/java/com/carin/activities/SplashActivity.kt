package com.carin.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.carin.R
import com.carin.utils.AuthUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        lifecycleScope.launch {
            delay(1500)
            val userAuth = AuthUtils.getUserAuth(this@SplashActivity)
            if (userAuth != null) {
                if (AuthUtils.isAuthenticated(this@SplashActivity)) {
                    navigateToHome()
                }
                else {
                    AuthUtils.refreshToken(this@SplashActivity, userAuth.refreshToken).collect { result ->
                        if (result) {
                            withContext(Dispatchers.Main) { navigateToHome() }
                        }
                    }
                }
            }
            else {
                withContext(Dispatchers.Main) { navigateToLogin() }
            }
        }
    }

    private fun navigateToHome() {
        val mainIntent = Intent(this@SplashActivity, HomeActivity::class.java)
        startActivity(mainIntent)
        finish()
    }

    private fun navigateToLogin() {
        val mainIntent = Intent(this@SplashActivity, LoginActivity::class.java)
        startActivity(mainIntent)
        finish()
    }
}
