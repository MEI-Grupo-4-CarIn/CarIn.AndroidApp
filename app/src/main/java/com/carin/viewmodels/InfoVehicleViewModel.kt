package com.carin.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.carin.data.repositories.VehicleRepository
import com.carin.domain.enums.VehicleStatus
import com.carin.domain.models.VehicleModel
import com.carin.utils.Resource
import kotlinx.coroutines.launch

class InfoVehicleViewModel(private val repository: VehicleRepository) : ViewModel() {

    private val _uiVehiclesState = MutableLiveData<Resource<List<VehicleModel>>>()
    val uiVehiclesState: LiveData<Resource<List<VehicleModel>>> get() = _uiVehiclesState

    fun loadVehicles(status: VehicleStatus, search: String?, page: Int, pageSize: Int) {
        viewModelScope.launch {
            repository.getVehiclesList(search, status, page, pageSize).collect { result ->
                if (result is Resource.Success) {
                    _uiVehiclesState.value = Resource.Success(result.data ?: emptyList())
                } else {
                    _uiVehiclesState.value = result
                }
            }
        }
    }
}

class InfoVehicleViewModelFactory(private val repository: VehicleRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InfoVehicleViewModel::class.java)) {
            return InfoVehicleViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
