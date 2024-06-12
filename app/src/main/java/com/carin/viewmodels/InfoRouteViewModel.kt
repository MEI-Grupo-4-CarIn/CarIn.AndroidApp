package com.carin.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.carin.data.repositories.RouteRepository
import com.carin.domain.models.RouteModel
import com.carin.domain.models.RouteUpdateModel
import com.carin.utils.Resource
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class InfoRouteViewModel(
    private val routeRepository: RouteRepository
) : ViewModel() {

    private val _uiDetailsState = MutableLiveData<Resource<RouteModel>>()
    val uiDetailsState: LiveData<Resource<RouteModel>> get() = _uiDetailsState
    private val _routeUpdateState = MutableLiveData<Resource<Boolean>>()
    val routeUpdateState: LiveData<Resource<Boolean>> get() = _routeUpdateState

    private val _uiRouteDeleteState = MutableLiveData<Resource<Boolean>>()
    val uiRouteDeleteState: LiveData<Resource<Boolean>> get() = _uiRouteDeleteState

    fun loadRouteDetails(routeId: String) {
        viewModelScope.launch {
            routeRepository.getRouteById(routeId, true).collect { result ->
                val route = result.data
                route?.let {
                    route.user?.age = it.user?.let { it1 -> calculateAge(it1.birthDate) }
                }

                _uiDetailsState.value = Resource.Success(route)
            }
        }
    }

    fun updateRoute(routeUpdateModel: RouteUpdateModel) {
        viewModelScope.launch {
            routeRepository.updateRoute(routeUpdateModel).collect { result ->
                _routeUpdateState.value = result
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

    fun deleteRoute(routeId: String) {
        viewModelScope.launch {
            routeRepository.deleteRoute(routeId).collect { result ->
                _uiRouteDeleteState.value = result
            }
        }
    }
}

class InfoRouteViewModelFactory(
    private val routeRepository: RouteRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InfoRouteViewModel::class.java)) {
            return InfoRouteViewModel(routeRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
