package com.carin.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.carin.data.repositories.RouteRepository
import com.carin.domain.models.RouteCreationModel
import com.carin.utils.Resource
import kotlinx.coroutines.launch

class CreateRouteViewModel(private val repository: RouteRepository) : ViewModel() {

    private val _creationState = MutableLiveData<Resource<String>>()
    val creationState: LiveData<Resource<String>> get() = _creationState

    fun createRoute(routeModel: RouteCreationModel) {
        viewModelScope.launch {
            repository.createRoute(routeModel).collect { result ->
                _creationState.value = result
            }
        }
    }
}

class CreateRouteViewModelFactory(private val repository: RouteRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateRouteViewModel::class.java)) {
            return CreateRouteViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}