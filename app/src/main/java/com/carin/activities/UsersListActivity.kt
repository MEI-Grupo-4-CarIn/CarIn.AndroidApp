package com.carin.activities

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.carin.R
import com.carin.di.RepositoryModule
import com.carin.fragments.MainUsersListFragment
import com.carin.viewmodels.UsersViewModel
import com.carin.viewmodels.UsersViewModelFactory
import com.carin.viewmodels.events.UsersListEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class UsersListActivity : AppCompatActivity() {
    private lateinit var viewModel: UsersViewModel
    private var searchJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users_list)

        if(savedInstanceState == null){
            supportFragmentManager.beginTransaction()
                .add(R.id.container, MainUsersListFragment())
                .commitNow()
        }

        val iconImageView: ImageView = findViewById(R.id.iconImageView)
        iconImageView.setOnClickListener {
            val intent = Intent(this, InfoVehicleActivity::class.java)
            startActivity(intent)
        }

        val searchEditText: EditText = findViewById(R.id.searchEditText)

        val userRepository = RepositoryModule.provideUserRepository(this)
        val factory = UsersViewModelFactory(userRepository)
        viewModel = ViewModelProvider(this, factory)[UsersViewModel::class.java]

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

    private fun performSearch(query: String) {
        viewModel.onEvent(UsersListEvent.UpdateSearch(query))
    }
}
