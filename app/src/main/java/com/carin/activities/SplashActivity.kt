package com.carin.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.carin.R
import com.carin.utils.AuthUtils
import kotlinx.coroutines.runBlocking

class SplashActivity : AppCompatActivity() {

    private val SPLASH_DISPLAY_LENGTH = 2000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler().postDelayed({
            val userAuth = AuthUtils.getUserAuth(this)
            if (userAuth != null) {
                if (!AuthUtils.isAuthenticated(this)) {
                    runBlocking {
                        AuthUtils.refreshTokenBlocking(this@SplashActivity, userAuth.refreshToken)
                    }

                    if (AuthUtils.isAuthenticated(this)) {
                        val mainIntent = Intent(this@SplashActivity, HomeActivity::class.java)
                        startActivity(mainIntent)
                        finish()
                    }
                }
            }
            else {
                val mainIntent = Intent(this@SplashActivity, LoginActivity::class.java)
                startActivity(mainIntent)
                finish()
            }
        }, SPLASH_DISPLAY_LENGTH)
    }
}
