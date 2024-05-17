package com.carin.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.carin.R

class UserActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        if(savedInstanceState == null){
            supportFragmentManager.beginTransaction()
                .add(R.id.container, MainFragmentUser())
                .commitNow()
        }

        val iconImageView: ImageView = findViewById(R.id.iconImageView)

        iconImageView.setOnClickListener {
            val intent = Intent(this, InfoVehicleActivity::class.java)
            startActivity(intent)
        }

    }

}
