package com.carin.activities

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.carin.R
import com.carin.adapter.ApprovalAdapter
import com.carin.adapter.HomeNotificationAdapter
import com.carin.adapter.LatestInformationAdapter
import com.carin.adapter.SchedulingHomeAdapter
import com.carin.adapter.UsersHomeAdapter
import com.carin.di.RepositoryModule
import com.carin.domain.enums.Role
import com.carin.domain.models.UserModel
import com.carin.utils.AuthUtils
import com.carin.utils.ItemSpacingDecoration
import com.carin.utils.Resource
import com.carin.viewmodels.InfoUserViewModel
import com.carin.viewmodels.InfoUserViewModelFactory

class HomeActivity : AppCompatActivity() {

    private var isRotated = false
    private lateinit var adapter: HomeNotificationAdapter
    private lateinit var userId: String
    private lateinit var notifications: List<Notification>
    private lateinit var adapterInformation: LatestInformationAdapter
    private lateinit var latestInformations: List<LatestInformation>
    private lateinit var adapterApproval: ApprovalAdapter
    private lateinit var adapterscheduling: SchedulingHomeAdapter
    private lateinit var schedulings: List<Schedulings>
    private lateinit var adapterUser: UsersHomeAdapter
    private lateinit var infoUserViewModel: InfoUserViewModel
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

       val userAuths = AuthUtils.getUserAuth(this)

        userAuths?.let {
            adjustUIBasedOnRole(it.role)
        }

        val userIdFromIntent = intent.getStringExtra("userId") ?: ""
        userId = userIdFromIntent

        progressBar = findViewById(R.id.progressBar)
        val errorTextView = findViewById<TextView>(R.id.errorTextView)
        val helloTextView: TextView = findViewById(R.id.textViewHello)
        val userAuth = AuthUtils.getUserAuth(this)
        val helloText = getString(R.string.hello, userAuth?.firstName)
        helloTextView.text = helloText

        val userRepository = RepositoryModule.provideUserRepository(this)
        val routeRepository = RepositoryModule.provideRouteRepository(this)
        val infoUserViewModelFactory = InfoUserViewModelFactory(userRepository, routeRepository)
        infoUserViewModel = ViewModelProvider(this, infoUserViewModelFactory).get(InfoUserViewModel::class.java)

        val recyclerView1: RecyclerView = findViewById(R.id.recyclerView1)
        recyclerView1.addItemDecoration(ItemSpacingDecoration(10))
        recyclerView1.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        notifications = getNotifications()
        adapter = HomeNotificationAdapter(notifications)
        recyclerView1.adapter = adapter
        adapter.setOnItemClickListener(object : HomeNotificationAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val intent = Intent(this@HomeActivity, InfoRouteActivity::class.java)
                startActivity(intent)
            }
        })

        prepareApprovalsRecyclerView()

        val recyclerView2: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView2.addItemDecoration(ItemSpacingDecoration(10))
        recyclerView2.layoutManager = LinearLayoutManager(this)
        latestInformations = getLatestInformation()
        adapterInformation = LatestInformationAdapter(latestInformations)
        recyclerView2.adapter = adapterInformation

        val recyclerView3: RecyclerView = findViewById(R.id.recyclerView3)
        recyclerView3.addItemDecoration(ItemSpacingDecoration(10))
        recyclerView3.layoutManager = LinearLayoutManager(this)
        schedulings = getSchedulings()
        adapterscheduling = SchedulingHomeAdapter(schedulings)
        recyclerView3.adapter = adapterscheduling
        adapterscheduling.setOnItemClickListener(object : SchedulingHomeAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val intent = Intent(this@HomeActivity, InfoSchedulingActivity::class.java)
                startActivity(intent)
            }
        })

        prepareUsersRecyclerView()

        val imageViewInfo = findViewById<ImageView>(R.id.imageViewInfo)
        imageViewInfo.setOnClickListener {
            val intent = Intent(this, InfoActivity::class.java)
            startActivity(intent)
        }

        val moreUsers: TextView = findViewById(R.id.textViewSeeMore3)
        moreUsers.setOnClickListener {
            val intent = Intent(this, UsersListActivity::class.java)
            overridePendingTransition(R.animator.slide_in_right, R.animator.slide_out_left)
            startActivity(intent)
        }

        val moreNotifications: TextView = findViewById(R.id.textViewSeeMore1)
        moreNotifications.setOnClickListener {
            val intent = Intent(this, NotificationActivity::class.java)
            overridePendingTransition(R.animator.slide_in_right, R.animator.slide_out_left)
            startActivity(intent)
        }

        val moreSchedulings: TextView = findViewById(R.id.textViewSeeMore)
        moreSchedulings.setOnClickListener {
            val intent = Intent(this, CalendarActivity::class.java)
            overridePendingTransition(R.animator.slide_in_right, R.animator.slide_out_left)
            startActivity(intent)
        }

        // Prepare the Menu
        prepareMenu()
    }

    override fun onResume() {
        super.onResume()

        infoUserViewModel.loadUsersForApproval()
    }

    private fun prepareMenu() {
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
                        duration = 300
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
                        duration = 300
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

    private fun prepareApprovalsRecyclerView() {
        val recyclerViewApprovals = findViewById<RecyclerView>(R.id.recyclerViewApprovals)
        val emptyTextViewApprovals = findViewById<TextView>(R.id.emptyTextViewApprovals)
        val progressBarApprovals = findViewById<ProgressBar>(R.id.progressBarApprovals)

        recyclerViewApprovals.addItemDecoration(ItemSpacingDecoration(10))
        recyclerViewApprovals.layoutManager = LinearLayoutManager(this)
        adapterApproval = ApprovalAdapter(mutableListOf(), this, infoUserViewModel)
        recyclerViewApprovals.adapter = adapterApproval

        var usersList: List<UserModel>
        infoUserViewModel.uiPendingUsersState.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    emptyTextViewApprovals.visibility = View.GONE
                    progressBarApprovals.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    usersList = resource.data ?: emptyList()
                    if (usersList.isEmpty()) {
                        emptyTextViewApprovals.visibility = View.VISIBLE
                        recyclerViewApprovals.visibility = View.GONE
                    } else {
                        emptyTextViewApprovals.visibility = View.GONE
                        recyclerViewApprovals.visibility = View.VISIBLE
                        adapterApproval.updateUsers(usersList)

                        recyclerViewApprovals.post {
                            val itemHeight = resources.getDimensionPixelSize(R.dimen.item_height)
                            val maxHeight = resources.getDimensionPixelSize(R.dimen.max_height)

                            val totalHeight = itemHeight * usersList.size
                            val finalHeight = if (totalHeight > maxHeight) maxHeight else totalHeight

                            recyclerViewApprovals.layoutParams.height = finalHeight
                            recyclerViewApprovals.requestLayout()
                        }
                    }

                    progressBarApprovals.visibility = View.GONE
                }
                is Resource.Error -> {
                    emptyTextViewApprovals.visibility = View.VISIBLE
                    progressBarApprovals.visibility = View.GONE
                }
            }
        }

        infoUserViewModel.loadUsersForApproval()
    }

    private fun prepareUsersRecyclerView() {
        val recyclerViewUsers = findViewById<RecyclerView>(R.id.recyclerViewUsers)
        val emptyTextViewUsers = findViewById<TextView>(R.id.emptyTextViewUsers)

        recyclerViewUsers.addItemDecoration(ItemSpacingDecoration(10))
        recyclerViewUsers.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        adapterUser = UsersHomeAdapter(mutableListOf())
        recyclerViewUsers.adapter = adapterUser

        infoUserViewModel.uiUsersState.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    val users = resource.data
                    if (users.isNullOrEmpty()) {
                        emptyTextViewUsers.visibility = View.VISIBLE
                        recyclerViewUsers.visibility = View.GONE
                    } else {
                        emptyTextViewUsers.visibility = View.GONE
                        recyclerViewUsers.visibility = View.VISIBLE
                        adapterUser.updateUsers(users)
                    }
                    progressBar.visibility = View.GONE
                }
                is Resource.Error -> {
                    emptyTextViewUsers.visibility = View.GONE
                    progressBar.visibility = View.GONE
                }
            }
        }

        infoUserViewModel.loadUsersHome(userId)
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
        findViewById<View>(R.id.linearLayout6).visibility = View.GONE
        findViewById<View>(R.id.recyclerViewApprovals).visibility = View.GONE
        findViewById<View>(R.id.thirdLineLinearLayout).visibility = View.GONE

        findViewById<View>(R.id.linearLayout2).visibility = View.GONE
        findViewById<View>(R.id.linear_layout_latest_information).visibility = View.GONE
        findViewById<View>(R.id.recyclerView).visibility = View.GONE
        findViewById<View>(R.id.fourthLineLinearLayout).visibility = View.GONE

        findViewById<View>(R.id.buttonMore).visibility = View.GONE

    }

    private fun getNotifications(): List<Notification> {

        val notifications = mutableListOf<Notification>()
        notifications.add(Notification(R.drawable.route, "Nova Rota","Itália", "Há 2 dias"))
        notifications.add(Notification(R.drawable.route, "Nova Rota","Itália", "Há 2 dias"))
        notifications.add(Notification(R.drawable.route, "Nova Rota","Itália", "Há 2 dias"))
        notifications.add(Notification(R.drawable.route, "Nova Rota","Itália", "Há 2 dias"))

        return notifications
    }
    data class Notification(val imageResource: Int, val notificationType: String,  val country: String, val temp: String)

    private fun getLatestInformation(): List<LatestInformation> {

        val latestInformations = mutableListOf<LatestInformation>()
        latestInformations.add(LatestInformation(R.drawable.ic_check, "Rota Concluída","Bruno Joaquim concluiu a viagem até Itália."))
        latestInformations.add(LatestInformation(R.drawable.ic_error, "Rota Concluída","Bruno Joaquim concluiu a viagem até Itália e voltou para Portugal."))
        latestInformations.add(LatestInformation(R.drawable.ic_error, "Rota Concluída","Bruno Joaquim concluiu a viagem até Itália.Bruno Joaquim concluiu a viagem até Itália.Bruno Joaquim concluiu a viagem até Itália."))
        latestInformations.add(LatestInformation(R.drawable.ic_check, "Rota Concluída","Bruno Joaquim concluiu a viagem até Itália."))

        return latestInformations
    }
    data class LatestInformation(val icon: Int, val typeInformation: String,  val information: String)

    private fun getSchedulings(): List<Schedulings> {

        val schedulings = mutableListOf<Schedulings>()
        schedulings.add(Schedulings("12:00", "Pagamento Seguro", "12:00 - 13:00"))
        schedulings.add(Schedulings("12:00", "Bruno vai buscar carro ao Porto", "12:00 - 13:00"))
        schedulings.add(Schedulings("12:00", "Pagamento Seguro", "12:00 - 13:00"))
        schedulings.add(Schedulings("12:00", "Bruno vai buscar carro ao Porto", "12:00 - 13:00"))

        return schedulings
    }
    data class Schedulings(val hour: String, val inf: String, val otherHour: String)
}



