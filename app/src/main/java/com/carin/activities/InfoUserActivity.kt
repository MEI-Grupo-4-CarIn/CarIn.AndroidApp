package com.carin.activities

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
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
import com.carin.fragments.MainFragmentUserInfo
import com.carin.utils.AuthUtils
import com.carin.viewmodels.InfoUserViewModel
import com.carin.viewmodels.InfoUserViewModelFactory

class InfoUserActivity : AppCompatActivity() {

    private var isRotated = false
    private lateinit var infoUserContainer: FrameLayout
    private lateinit var optionsIcon: ImageView
    private lateinit var viewModel: InfoUserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_user)

        val idFromList = intent.getIntExtra("id", -1)
        val userAuth = AuthUtils.getUserAuth(this)
        val isOwnProfile = idFromList == -1 || idFromList == userAuth?.userId

        userAuth?.let {
            adjustUIBasedOnRole(it.role)
        }

        infoUserContainer = findViewById(R.id.infoUserContainer)
        optionsIcon = findViewById(R.id.optionsIcon)
        val goBackIcon = findViewById<ImageView>(R.id.infoUserGoBackIcon)

        // Adjust the UI to show the footer and options menu only for the own profile
        if (isOwnProfile) {
            val layoutParams = infoUserContainer.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.bottomMargin = dpToPx(88, this)
            infoUserContainer.layoutParams = layoutParams

            findViewById<View>(R.id.footerLinearLayout).visibility = View.VISIBLE
            optionsIcon.visibility = View.VISIBLE
            goBackIcon.visibility = View.GONE

            prepareMenu()
            prepareOptionsMenu()
        } else {
            goBackIcon.setOnClickListener {
                finish()
            }
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
        val layoutAddUser = findViewById<RelativeLayout>(R.id.layoutAddUser)

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
                layoutAddUser.visibility = View.INVISIBLE
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
                layoutAddUser.visibility = View.VISIBLE
            }
            isRotated = !isRotated
        }

        layoutAddUser.setOnClickListener {
            val intent = Intent(this, NewUserActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.animator.slide_up, 0)
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
                        startActivity(Intent(this, EditUserActivity::class.java))
                        true
                    }
                    R.id.logOut -> {
                        AuthUtils.clearUserOnSharedPreferences(this)
                        startActivity(Intent(this, LoginActivity::class.java))
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
}
