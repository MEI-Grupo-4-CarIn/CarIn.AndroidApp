package com.carin.activities

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.carin.R

class InfoUserActivity : AppCompatActivity() {

    private var isRotated = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_user)

        if(savedInstanceState == null){
            supportFragmentManager.beginTransaction()
                .add(R.id.container, MainFragmentUserInfo())
                .commitNow()
        }
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

        val userInfoFragment = layoutInflater.inflate(R.layout.user_info_fragment, null)

        val nameText: TextView = findViewById(R.id.textView1)
        val emailText: TextView = userInfoFragment.findViewById(R.id.textView2)
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

        val buttonRoute: ImageView = findViewById(R.id.buttonRoute)

        buttonRoute.setOnClickListener {
            val intent = Intent(this, RouteActivity::class.java)
            startActivity(intent)
            finish()
        }

        val buttonVehicle: ImageView = findViewById(R.id.buttonVehicle)

        buttonVehicle.setOnClickListener {
            val intent = Intent(this, VehicleActivity::class.java)
            startActivity(intent)
        }

        val buttonHome: ImageView = findViewById(R.id.buttonHome)

        buttonHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        val buttonPerson: ImageView = findViewById(R.id.buttonPerson)

        buttonPerson.setOnClickListener {
            val intent = Intent(this, InfoUserActivity::class.java)
            startActivity(intent)
        }

        val buttonMore = findViewById<ImageButton>(R.id.buttonMore)
        val layoutNewAppointment = findViewById<RelativeLayout>(R.id.layoutNewAppointment)
        val layoutAddRoute = findViewById<RelativeLayout>(R.id.layoutAddRoute)
        val layoutAddVehicle = findViewById<RelativeLayout>(R.id.layoutAddVehicle)
        val layoutAddUser = findViewById<RelativeLayout>(R.id.layoutAddUser)

        buttonMore.setOnClickListener {
            if (isRotated) {
                val rotateAnimator = ObjectAnimator.ofFloat(buttonMore, "rotation", 45f, 0f)
                    .apply {
                        duration = 500
                        interpolator = AccelerateDecelerateInterpolator()
                    }

                val animatorSet = AnimatorSet()
                animatorSet.play(rotateAnimator)
                animatorSet.start()

                layoutNewAppointment.visibility = View.INVISIBLE
                layoutAddRoute.visibility = View.INVISIBLE
                layoutAddVehicle.visibility = View.INVISIBLE
                layoutAddUser.visibility = View.INVISIBLE
            } else {
                val rotateAnimator = ObjectAnimator.ofFloat(buttonMore, "rotation", 0f, 45f)
                    .apply {
                        duration = 500
                        interpolator = AccelerateDecelerateInterpolator()
                    }

                val animatorSet = AnimatorSet()
                animatorSet.play(rotateAnimator)
                animatorSet.start()

                layoutNewAppointment.visibility = View.VISIBLE
                layoutAddRoute.visibility = View.VISIBLE
                layoutAddVehicle.visibility = View.VISIBLE
                layoutAddUser.visibility = View.VISIBLE
            }
            isRotated = !isRotated
        }

        layoutAddUser.setOnClickListener {
            val intent = Intent(this, NewUserActivity::class.java)
            startActivity(intent)
        }


        layoutAddVehicle.setOnClickListener {
            val intent = Intent(this, NewVehicleActivity::class.java)
            startActivity(intent)
        }

        layoutAddRoute.setOnClickListener {
            val intent = Intent(this, NewRouteActivity::class.java)
            startActivity(intent)
        }

        layoutNewAppointment.setOnClickListener {
            val intent = Intent(this, NewSchedulingActivity::class.java)
            startActivity(intent)
        }

    }

}
