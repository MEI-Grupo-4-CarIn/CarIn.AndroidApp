package com.carin.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.carin.data.repositories.RouteRepository
import com.carin.data.repositories.VehicleRepository
import com.carin.domain.enums.VehicleStatus
import com.carin.domain.models.UserModel
import com.carin.domain.models.VehicleModel
import com.carin.domain.models.VehicleUpdateModel
import com.carin.utils.Resource
import com.carin.viewmodels.states.RoutesListState
import kotlinx.coroutines.launch

class InfoVehicleViewModel(
    private val vehicleRepository: VehicleRepository,
    private val routeRepository: RouteRepository
) : ViewModel() {

    private val _uiVehiclesState = MutableLiveData<Resource<List<VehicleModel>>>()
    val uiVehiclesState: LiveData<Resource<List<VehicleModel>>> get() = _uiVehiclesState
    private val _uiDetailsState = MutableLiveData<Resource<VehicleModel>>()
    val uiDetailsState: LiveData<Resource<VehicleModel>> get() = _uiDetailsState
    private val _uiRoutesState = MutableLiveData<RoutesListState>()
    val uiRoutesState: LiveData<RoutesListState> get() = _uiRoutesState
    private val _uiUsersState = MutableLiveData<Resource<List<UserModel>>>()
    val uiUsersState: LiveData<Resource<List<UserModel>>> get() = _uiUsersState
    private val _vehicleUpdateState = MutableLiveData<Resource<Boolean>>()
    val vehicleUpdateState: LiveData<Resource<Boolean>> get() = _vehicleUpdateState
    private val _uiVehicleDeleteState = MutableLiveData<Resource<Boolean>>()
    val uiVehicleDeleteState: LiveData<Resource<Boolean>> get() = _uiVehicleDeleteState



    fun loadVehicles(status: VehicleStatus, search: String?, page: Int, pageSize: Int) {
        viewModelScope.launch {
            vehicleRepository.getVehiclesList(search, status, page, pageSize).collect { result ->
                if (result is Resource.Success) {
                    _uiVehiclesState.value = Resource.Success(result.data ?: emptyList())
                } else {
                    _uiVehiclesState.value = result
                }
            }
        }
    }

    fun loadVehicleDetails(vehicleId: String) {
        viewModelScope.launch {
            vehicleRepository.getVehicleById(vehicleId, true).collect { result ->
                _uiDetailsState.value = result
            }
        }
    }

    fun updateVehicle(vehicleUpdateModel: VehicleUpdateModel) {
        viewModelScope.launch {
            vehicleRepository.updateVehicle(vehicleUpdateModel).collect { result ->
                _vehicleUpdateState.value = result
            }
        }
    }

    fun loadRoutesForVehicle(vehicleId: String, quantity: Int = 6) {
        viewModelScope.launch {
            routeRepository.getRoutesList(
                null,
                null,
                1,
                quantity,
                null,
                vehicleId
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

    fun loadUsersForVehicle(vehicleId: String, quantity: Int = 6) {
        viewModelScope.launch {
            routeRepository.getRoutesList(
                null,
                null,
                1,
                quantity,
                null,
                vehicleId
            ).collect { result ->
                when (result) {
                    is Resource.Loading -> _uiUsersState.value = Resource.Loading()
                    is Resource.Success -> {
                        val routes = result.data ?: emptyList()
                        val users = routes.mapNotNull { it.user }
                        _uiUsersState.value = Resource.Success(users)
                    }
                    is Resource.Error -> _uiUsersState.value = Resource.Error(result.message ?: "Unknown error")
                }
            }
        }
    }

    fun deleteVehicle(vehicleId: String) {
        viewModelScope.launch {
            var canDelete = true

            routeRepository.getRoutesList(
                null,
                null,
                1,
                1,
                null,
                vehicleId
            ).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiVehicleDeleteState.value = Resource.Loading()
                    }
                    is Resource.Success -> {
                        canDelete = result.data?.isEmpty() == true
                    }
                    is Resource.Error -> {
                        _uiVehicleDeleteState.value = Resource.Error("error_fetch_routes_for_vehicle")
                    }
                }
                if (result is Resource.Success) {
                    if (result.data?.isNotEmpty() == true) {
                        canDelete = false
                    }
                }
            }

            if (canDelete) {
                vehicleRepository.deleteVehicle(vehicleId).collect { result ->
                    _uiVehicleDeleteState.value = result
                }
            } else {
                _uiVehicleDeleteState.value = Resource.Error("error_vehicle_cannot_be_deleted")
            }
        }
    }
}

class InfoVehicleViewModelFactory(
    private val vehicleRepository: VehicleRepository,
    private val routeRepository: RouteRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InfoVehicleViewModel::class.java)) {
            return InfoVehicleViewModel(vehicleRepository, routeRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
