package com.carin.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.carin.data.repositories.RouteRepository
import com.carin.data.repositories.UserRepository
import com.carin.domain.enums.Role
import com.carin.domain.models.UserModel
import com.carin.domain.models.UserUpdateModel
import com.carin.utils.Resource
import com.carin.viewmodels.states.RoutesListState
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class InfoUserViewModel(
    private val userRepository: UserRepository,
    private val routeRepository: RouteRepository
) : ViewModel() {

    private val _uiUsersState = MutableLiveData<Resource<List<UserModel>>>()
    val uiUsersState: LiveData<Resource<List<UserModel>>> get() = _uiUsersState
    private val _uiPendingUsersState = MutableLiveData<Resource<List<UserModel>>>()
    val uiPendingUsersState: LiveData<Resource<List<UserModel>>> get() = _uiPendingUsersState
    private val _uiDetailsState = MutableLiveData<Resource<UserModel>>()
    val uiDetailsState: LiveData<Resource<UserModel>> get() = _uiDetailsState
    private val _uiRoutesState = MutableLiveData<RoutesListState>()
    val uiRoutesState: LiveData<RoutesListState> get() = _uiRoutesState
    private val _userUpdateState = MutableLiveData<Resource<Boolean>>()
    val userUpdateState: LiveData<Resource<Boolean>> get() = _userUpdateState

    private val _userApprovalState = MutableLiveData<Resource<Boolean>>()
    val userApprovalState: LiveData<Resource<Boolean>> get() = _userApprovalState

    fun loadUsers(role: Role, search: String?, page: Int, pageSize: Int) {
        viewModelScope.launch {
            userRepository.getUsersList(search, role, page, pageSize).collect { result ->
                if (result is Resource.Success) {
                    _uiUsersState.value = Resource.Success(result.data ?: emptyList())
                } else {
                    _uiUsersState.value = result
                }
            }
        }
    }

    fun loadUserDetails(userId: Int) {
        viewModelScope.launch {
            userRepository.getUserById(userId, true).collect { result ->
                if (result is Resource.Success) {
                    val user = result.data
                    user?.let {
                        it.age = calculateAge(it.birthDate)
                        _uiDetailsState.value = Resource.Success(it)
                    }
                } else {
                    _uiDetailsState.value = result
                }
            }
        }
    }

    fun loadRoutesForUser(userId: Int, quantity: Int = 6) {
        viewModelScope.launch {
            routeRepository.getRoutesList(
                null,
                null,
                1,
                quantity,
                userId,
                null
            ).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiRoutesState.value = RoutesListState.Loading()
                    }
                    is Resource.Success -> {
                        _uiRoutesState.value = RoutesListState.Success(
                            routes = result.data ?: emptyList(),
                            isEmpty = result.data.isNullOrEmpty(),
                        )
                    }
                    is Resource.Error -> {
                        _uiRoutesState.value = RoutesListState.Error(result.message ?: "Unknown error")
                    }
                }
            }
        }
    }

    fun updateUser(userUpdateModel: UserUpdateModel) {
        viewModelScope.launch {
            userRepository.updateUser(userUpdateModel).collect { result ->
                _userUpdateState.value = result
            }
        }
    }

    fun loadUsersForApproval() {
        viewModelScope.launch {
            userRepository.getWaitingForApprovalUsersList(1, 6).collect { result ->
                if (result is Resource.Success) {
                    _uiPendingUsersState.value = Resource.Success(result.data ?: emptyList())
                } else {
                    _uiPendingUsersState.value = result
                }
            }
        }
    }

    fun approveUser(userId: Int, role: Role?) {
        viewModelScope.launch {
            userRepository.approveUser(userId, role).collect { result ->
                _userApprovalState.value = result
            }
        }
    }

    private fun calculateAge(dateOfBirth: Date): Int {
        val birthCalendar = Calendar.getInstance().apply {
            time = dateOfBirth
        }
        val today = Calendar.getInstance()
        var age = today.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR)
        if (today.get(Calendar.DAY_OF_YEAR) < birthCalendar.get(Calendar.DAY_OF_YEAR)) {
            age--
        }

        return age
    }
}

class InfoUserViewModelFactory(
    private val userRepository: UserRepository,
    private val routeRepository: RouteRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InfoUserViewModel::class.java)) {
            return InfoUserViewModel(userRepository, routeRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
