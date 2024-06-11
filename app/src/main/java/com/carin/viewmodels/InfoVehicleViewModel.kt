package com.carin.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.carin.data.repositories.RouteRepository
import com.carin.data.repositories.VehicleRepository
import com.carin.domain.enums.VehicleStatus
import com.carin.domain.models.VehicleModel
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
}

class InfoVehicleViewModelFactory(
    private val vehicleRepository: VehicleRepository,
    private val routeRepository: RouteRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InfoVehicleViewModel::class.java)) {
            return InfoVehicleViewModel(vehicleRepository, routeRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
