package com.carin.activities

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Shader
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.view.ContextThemeWrapper
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.carin.R
import com.carin.di.RepositoryModule
import com.carin.domain.enums.Role
import com.carin.domain.models.UserAuth
import com.carin.fragments.MainFragmentUserInfo
import com.carin.utils.AuthUtils
import com.carin.viewmodels.InfoUserViewModel
import com.carin.viewmodels.InfoUserViewModelFactory
import java.io.File

class InfoUserActivity : AppCompatActivity() {

    private var isRotated = false
    private lateinit var infoUserContainer: FrameLayout
    private lateinit var optionsIcon: ImageView
    private lateinit var infoUserImageView: ImageView
    private lateinit var viewModel: InfoUserViewModel
    private var userAuth: UserAuth? = null
    private var isOwnProfile = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_user)

        val idFromList = intent.getIntExtra("id", -1)
        val isFromList = idFromList != -1
        userAuth = AuthUtils.getUserAuth(this)
        isOwnProfile = !isFromList || idFromList == userAuth?.userId

        userAuth?.let {
            adjustUIBasedOnRole(it.role)
        }

        infoUserContainer = findViewById(R.id.infoUserContainer)
        optionsIcon = findViewById(R.id.optionsIcon)
        infoUserImageView = findViewById(R.id.infoUserImageView)
        val goBackIcon = findViewById<ImageView>(R.id.infoUserGoBackIcon)
        goBackIcon.setOnClickListener {
            finish()
        }

        // Adjust the UI to show the footer and options menu only for the own profile
        if (isOwnProfile) {
            val layoutParams = infoUserContainer.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.bottomMargin = dpToPx(88, this)
            infoUserContainer.layoutParams = layoutParams

            findViewById<View>(R.id.footerLinearLayout).visibility = View.VISIBLE
            optionsIcon.visibility = View.VISIBLE

            if (!isFromList)
                goBackIcon.visibility = View.GONE

            prepareMenu()
            prepareOptionsMenu()

            setImageForUser()
        }

        if(savedInstanceState == null){
            supportFragmentManager.beginTransaction()
                .add(R.id.infoUserContainer, MainFragmentUserInfo())
                .commitNow()
        }

        val infoUserNameTxt = findViewById<TextView>(R.id.infoUserNameTxt)
        if (isOwnProfile) {
            infoUserNameTxt.text = "${userAuth?.firstName} ${userAuth?.lastName}"
        } else {
            val userName = intent.getStringExtra("name")
            infoUserNameTxt.text = userName
        }

        val userRepository = RepositoryModule.provideUserRepository(this)
        val routeRepository = RepositoryModule.provideRouteRepository(this)
        val factory = InfoUserViewModelFactory(userRepository, routeRepository)
        viewModel = ViewModelProvider(this, factory)[InfoUserViewModel::class.java]
    }

    override fun onResume() {
        super.onResume()

        val infoUserNameTxt = findViewById<TextView>(R.id.infoUserNameTxt)
        if (isOwnProfile) {
            userAuth = AuthUtils.getUserAuth(this)
            infoUserNameTxt.text = "${userAuth?.firstName} ${userAuth?.lastName}"

            setImageForUser()
        } else {
            val userName = intent.getStringExtra("name")
            infoUserNameTxt.text = userName
        }
    }

    private fun prepareMenu() {
        val buttonHome = findViewById<LinearLayout>(R.id.linearLayoutHome)
        buttonHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        val buttonRoutes = findViewById<LinearLayout>(R.id.linearLayoutRoutes)
        buttonRoutes.setOnClickListener {
            val intent = Intent(this, RoutesListActivity::class.java)
            startActivity(intent)
            finish()
        }

        val buttonVehicles = findViewById<LinearLayout>(R.id.linearLayoutVehicles)
        buttonVehicles.setOnClickListener {
            val intent = Intent(this, VehiclesListActivity::class.java)
            startActivity(intent)
            finish()
        }

        val buttonMore = findViewById<LinearLayout>(R.id.linearLayoutMore)
        val layoutNewAppointment = findViewById<RelativeLayout>(R.id.layoutNewAppointment)
        val layoutAddRoute = findViewById<RelativeLayout>(R.id.layoutAddRoute)
        val layoutAddVehicle = findViewById<RelativeLayout>(R.id.layoutAddVehicle)

        buttonMore.setOnClickListener {
            if (isRotated) {
                val rotateAnimator = ObjectAnimator.ofFloat(buttonMore, "rotation", 45f, 0f)
                    .apply {
                        duration = 500
                        interpolator = AccelerateDecelerateInterpolator()
                    }

                val animatorSet = AnimatorSet()
                animatorSet.playTogether(rotateAnimator)
                animatorSet.start()

                layoutNewAppointment.visibility = View.INVISIBLE
                layoutAddRoute.visibility = View.INVISIBLE
                layoutAddVehicle.visibility = View.INVISIBLE
            } else {
                val rotateAnimator = ObjectAnimator.ofFloat(buttonMore, "rotation", 0f, 45f)
                    .apply {
                        duration = 500
                        interpolator = AccelerateDecelerateInterpolator()
                    }

                val animatorSet = AnimatorSet()
                animatorSet.playTogether(rotateAnimator)
                animatorSet.start()

                layoutNewAppointment.visibility = View.VISIBLE
                layoutAddRoute.visibility = View.VISIBLE
                layoutAddVehicle.visibility = View.VISIBLE
            }
            isRotated = !isRotated
        }

        layoutAddVehicle.setOnClickListener {
            val intent = Intent(this, NewVehicleActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.animator.slide_up, 0)
        }

        layoutAddRoute.setOnClickListener {
            val intent = Intent(this, NewRouteActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.animator.slide_up, 0)
        }

        layoutNewAppointment.setOnClickListener {
            val intent = Intent(this, NewSchedulingActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.animator.slide_up, 0)
        }
    }

    private fun prepareOptionsMenu() {
        optionsIcon.setOnClickListener { view ->
            val popupMenu = PopupMenu(ContextThemeWrapper(this, R.style.PopupMenu), view)

            val editItem = popupMenu.menu.add(Menu.NONE, R.id.edit, Menu.NONE, "Edit")
            val logoutItem = popupMenu.menu.add(Menu.NONE, R.id.logOut, Menu.NONE, "Log Out")

            val editIcon = ContextCompat.getDrawable(this, R.drawable.ic_edit)
            editIcon?.setBounds(0, 0, editIcon.intrinsicWidth, editIcon.intrinsicHeight)
            val editIconSpan = editIcon?.let { ImageSpan(it, ImageSpan.ALIGN_BOTTOM) }
            val editItemTitle = SpannableString(" ${getString(R.string.edit)}")
            editItemTitle.setSpan(editIconSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            editItem.title = editItemTitle

            val logoutIcon = ContextCompat.getDrawable(this, R.drawable.ic_logout)
            logoutIcon?.setBounds(0, 0, logoutIcon.intrinsicWidth, logoutIcon.intrinsicHeight)
            val logoutIconSpan = logoutIcon?.let { ImageSpan(it, ImageSpan.ALIGN_BOTTOM) }
            val logoutItemTitle = SpannableString(" ${getString(R.string.logOut)}")
            logoutItemTitle.setSpan(logoutIconSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            logoutItem.title = logoutItemTitle

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.edit -> {
                        val intent = Intent(this, EditUserActivity::class.java)
                        if (userAuth != null)
                            intent.putExtra("userId", userAuth?.userId)

                        startActivity(intent)
                        true
                    }
                    R.id.logOut -> {
                        AuthUtils.clearUserOnSharedPreferences(this)

                        val intent = Intent(this, LoginActivity::class.java)
                        // Clears the activity stack
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
    }

    private fun adjustUIBasedOnRole(role: Role) {
        when (role) {
            Role.Admin -> showAdminComponents()
            Role.Manager -> showManagerComponents()
            Role.Driver -> showDriverComponents()
        }
    }

    private fun showAdminComponents() {

    }
    private fun showManagerComponents() {

    }

    private fun showDriverComponents() {
        findViewById<View>(R.id.buttonMore).visibility = View.GONE
    }

    private fun dpToPx(dp: Int, context: Context): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }

    private fun setImageForUser() {
        val imageView = findViewById<ImageView>(R.id.infoUserImageView)
        val userId = userAuth?.userId ?: return

        val profilePicDirectory = getDir("profile_pics", Context.MODE_PRIVATE)
        val imagePath = File(profilePicDirectory, "$userId.png").absolutePath

        if (File(imagePath).exists()) {
            val bitmap = BitmapFactory.decodeFile(imagePath)
            imageView.setImageBitmap(getCircularBitmap(bitmap))
        } else {
            imageView.setImageResource(R.drawable.ic_person_blue)
        }
    }

    private fun getCircularBitmap(bitmap: Bitmap): Bitmap {
        val size = bitmap.width.coerceAtLeast(bitmap.height)
        val scale = 2
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, size * scale, size * scale, true)
        val circleBitmap = Bitmap.createBitmap(size * scale, size * scale, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(circleBitmap)
        val paint = Paint().apply {
            isAntiAlias = true
            shader = BitmapShader(scaledBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        }
        val radius = (size * scale / 2).toFloat()
        canvas.drawCircle(radius, radius, radius, paint)
        return circleBitmap
    }
}
