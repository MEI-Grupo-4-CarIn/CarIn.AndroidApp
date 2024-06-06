package com.carin.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.carin.data.repositories.VehicleRepository
import com.carin.domain.enums.VehicleType
import com.carin.utils.Resource
import com.carin.viewmodels.events.VehiclesListEvent
import com.carin.viewmodels.states.VehiclesListState
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class VehiclesViewModel(private val repository: VehicleRepository) : ViewModel() {

    private val _uiState = MutableLiveData<VehiclesListState>()
    val uiState: LiveData<VehiclesListState> get() = _uiState
    private val _searchQuery = MutableLiveData<String?>()
    val searchQuery: LiveData<String?> get() = _searchQuery

    private val perPage = 50
    private var currentPage = mutableMapOf<VehicleType, Int>()
    private var isLoadingMore = mutableMapOf<VehicleType, Boolean>()
    private var hasMoreData = mutableMapOf<VehicleType, Boolean>()

    private var loadMoreJob: Job? = null

    init {
        // Initialize default values for all routeTypes
        VehicleType.entries.forEach { vehicleType ->
            currentPage[vehicleType] = 1
            isLoadingMore[vehicleType] = false
            hasMoreData[vehicleType] = true
        }
    }

    fun onEvent(event: VehiclesListEvent) {
        when (event) {
            is VehiclesListEvent.UpdateSearch -> {
                _searchQuery.value = event.searchQuery
            }
            is VehiclesListEvent.LoadVehicles -> {
                loadVehicles(event.vehicleType, event.page)
            }
            is VehiclesListEvent.LoadMoreVehicles -> {
                loadMoreVehicles(event.vehicleType)
            }
        }
    }

    private fun loadVehicles(vehicleType: VehicleType, page: Int = 1) {
        vehicleType.let { type ->
            currentPage[type] = page
            hasMoreData[type] = true

            viewModelScope.launch {
                repository.getVehiclesList(_searchQuery.value, VehicleType.toVehicleStatus(type), page, perPage).collect { result ->
                    when (result) {
                        is Resource.Loading<*> -> {
                            _uiState.value = VehiclesListState.Loading(type)
                        }
                        is Resource.Success<*> -> {
                            if (result.data.isNullOrEmpty() || result.data.size < perPage) {
                                hasMoreData[type] = false
                            }
                            _uiState.value = VehiclesListState.Success(
                                vehicles = result.data ?: emptyList(),
                                vehicleType = type,
                                isEmpty = result.data.isNullOrEmpty(),
                                isAppending = false
                            )
                        }
                        is Resource.Error -> {
                            _uiState.value = VehiclesListState.Error(type, result.message ?: "Unknown error")
                        }
                    }
                }
            }
        }
    }

    private fun loadMoreVehicles(vehicleType: VehicleType) {
        vehicleType.let { type ->
            if (isLoadingMore[type] == true
                || hasMoreData[type] == false
                || loadMoreJob?.isActive == true) {
                return
            }

            isLoadingMore[type] = true
            currentPage[type] = currentPage[type]?.plus(1) ?: 1

            loadMoreJob = viewModelScope.launch {
                try {
                    repository.getVehiclesList(_searchQuery.value, VehicleType.toVehicleStatus(type), currentPage[type] ?: 1, perPage).collect { result ->
                        when (result) {
                            is Resource.Loading -> {
                                _uiState.value = VehiclesListState.Loading(type)
                            }
                            is Resource.Success -> {
                                if (!result.waitForRemote && result.data.isNullOrEmpty()) {
                                    hasMoreData[type] = false
                                }
                                val currentData = (_uiState.value as? VehiclesListState.Success)?.vehicles?.toMutableList() ?: mutableListOf()

                                result.data?.let { currentData.addAll(it) }
                                _uiState.value = VehiclesListState.Success(
                                    vehicles = currentData,
                                    vehicleType = type,
                                    isEmpty = result.data.isNullOrEmpty(),
                                    isAppending = true
                                )
                            }
                            is Resource.Error -> {
                                _uiState.value = VehiclesListState.Error(type, result.message ?: "Unknown error")
                            }
                        }
                    }
                } finally {
                    isLoadingMore[type] = false
                }
            }
        }
    }
}

class VehiclesViewModelFactory(private val repository: VehicleRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VehiclesViewModel::class.java)) {
            return VehiclesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
