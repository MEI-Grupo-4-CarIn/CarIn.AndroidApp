package com.carin.activities

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.carin.R

class InfoUserActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_user)

        val iconImageView: ImageView = findViewById(R.id.iconImage)

        iconImageView.setOnClickListener {
            val intent = Intent(this, UserActivity::class.java)
            startActivity(intent)
        }

        val iconEditImageView: ImageView = findViewById(R.id.optionsIcon)

        iconEditImageView.setOnClickListener {
            val intent = Intent(this, EditUserActivity::class.java)
            startActivity(intent)
        }

        val nameText: TextView = findViewById(R.id.textView1)
        val emailText: TextView = findViewById(R.id.textView2)
        val personImageView: ImageView = findViewById(R.id.imageView)

        val name = intent.getStringExtra("name")
        val email = intent.getStringExtra("email")
        val personImageByteArray: ByteArray? = intent.getByteArrayExtra("imageResource")

        nameText.text = name
        emailText.text =email

        if (personImageByteArray != null) {
            val carBitmap = BitmapFactory.decodeByteArray(personImageByteArray, 0, personImageByteArray.size)

            personImageView.setImageBitmap(carBitmap)
        }


    }

}
