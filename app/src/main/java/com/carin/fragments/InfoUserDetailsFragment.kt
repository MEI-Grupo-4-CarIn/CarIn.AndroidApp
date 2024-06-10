package com.carin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.carin.R
import com.carin.utils.AuthUtils
import com.carin.utils.Resource
import com.carin.viewmodels.InfoUserViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class InfoUserDetailsFragment : Fragment() {

    private lateinit var viewModel: InfoUserViewModel
    private lateinit var progressBar: ProgressBar
    private var userId: Int? = null
    private val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.info_user_details_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBar = view.findViewById(R.id.progressBar)

        // Obtain the ViewModel from the Activity's ViewModelProvider
        viewModel = ViewModelProvider(requireActivity())[InfoUserViewModel::class.java]
        viewModel.uiDetailsState.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    resource.data?.let { user ->
                        view.findViewById<TextView>(R.id.infoUserDetailsNameTxt).text = "${user.firstName} ${user.lastName}"
                        view.findViewById<TextView>(R.id.infoUserDetailsEmailTxt).text = user.email
                        view.findViewById<TextView>(R.id.birthdayDetailsTextView).text = formatter.format(user.birthDate)
                        view.findViewById<TextView>(R.id.ageTextView).text = user.age.toString()
                        view.findViewById<TextView>(R.id.roleDetailsTextView).text = user.role.toString()
                    }

                    progressBar.visibility = View.GONE
                }
                is Resource.Error -> {
                    progressBar.visibility = View.GONE
                    val networkErrorMessage = getString(R.string.network_error)
                    Toast.makeText(requireContext(), networkErrorMessage, Toast.LENGTH_LONG).show()
                }
            }
        }

        // Load user details
        val userIdFromList = requireActivity().intent.getIntExtra("id", -1)
        if (userIdFromList == -1) {
            val userAuth = AuthUtils.getUserAuth(requireContext())
            if (userAuth != null) {
                userId = userAuth.userId
                viewModel.loadUserDetails(userAuth.userId)
            }
        } else {
            userId = userIdFromList
            viewModel.loadUserDetails(userIdFromList)
        }
    }

    override fun onResume() {
        super.onResume()
        userId?.let { viewModel.loadUserDetails(it) }
    }
}
