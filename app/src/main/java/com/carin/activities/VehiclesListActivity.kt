package com.carin.activities

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.carin.R
import com.carin.di.RepositoryModule
import com.carin.domain.enums.Role
import com.carin.fragments.MainFragmentVehicle
import com.carin.utils.AuthUtils
import com.carin.viewmodels.VehiclesListViewModel
import com.carin.viewmodels.VehiclesListViewModelFactory
import com.carin.viewmodels.events.VehiclesListEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class VehiclesListActivity : AppCompatActivity() {

    private var isRotated = false
    private lateinit var viewModel: VehiclesListViewModel
    private var searchJob : Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vehicles_list)

        val userAuths = AuthUtils.getUserAuth(this)

        userAuths?.let {
            adjustUIBasedOnRole(it.role)
        }

        if(savedInstanceState == null){
            supportFragmentManager.beginTransaction()
                .add(R.id.container, MainFragmentVehicle())
                .commitNow()
        }

        val searchEditText: EditText = findViewById(R.id.searchEditText)

        val vehicleRepository = RepositoryModule.provideVehicleRepository(this)
        val factory = VehiclesListViewModelFactory(vehicleRepository)
        viewModel = ViewModelProvider(this, factory)[VehiclesListViewModel::class.java]

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            // Close the keyboard when the search button is clicked
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(searchEditText.text.toString())
                searchEditText.clearFocus()
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
                true
            } else {
                false
            }
        }

        // Add a text watcher to handle text changes
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchJob?.cancel()
                searchJob = lifecycleScope.launch {
                    delay(500)
                    s?.let {
                        performSearch(it.toString())
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        prepareMenu()
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v: View? = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
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

        val buttonProfile = findViewById<LinearLayout>(R.id.linearLayoutProfile)
        buttonProfile.setOnClickListener {
            val intent = Intent(this, InfoUserActivity::class.java)
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

    private fun performSearch(query: String) {
        viewModel.onEvent(VehiclesListEvent.UpdateSearch(query))
    }
}
