package com.carin.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.carin.data.repositories.VehicleRepository
import com.carin.domain.models.VehicleCreationModel
import com.carin.utils.Resource
import kotlinx.coroutines.launch

class CreateVehicleViewModel(private val repository: VehicleRepository) : ViewModel() {

    private val _creationState = MutableLiveData<Resource<String>>()
    val creationState: LiveData<Resource<String>> get() = _creationState

    fun createVehicle(vehicleModel: VehicleCreationModel) {
        viewModelScope.launch {
            repository.createVehicle(vehicleModel).collect { result ->
                _creationState.value = result
            }
        }
    }
}

class CreateVehicleViewModelFactory(private val repository: VehicleRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateVehicleViewModel::class.java)) {
            return CreateVehicleViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
