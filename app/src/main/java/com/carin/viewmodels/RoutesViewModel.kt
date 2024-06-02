package com.carin.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.carin.data.repositories.RouteRepository
import com.carin.domain.enums.RouteType
import com.carin.utils.Resource
import com.carin.viewmodels.events.RoutesListEvent
import com.carin.viewmodels.states.RoutesListState
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class RoutesViewModel(private val repository: RouteRepository) : ViewModel() {

    private val _uiState = MutableLiveData<RoutesListState>()
    val uiState: LiveData<RoutesListState> get() = _uiState
    private val _searchQuery = MutableLiveData<String?>()
    val searchQuery: LiveData<String?> get() = _searchQuery

    private val perPage = 10
    private var currentPage = mutableMapOf<RouteType, Int>()
    private var isLoadingMore = mutableMapOf<RouteType, Boolean>()
    private var hasMoreData = mutableMapOf<RouteType, Boolean>()

    private var loadMoreJob: Job? = null

    init {
        // Initialize default values for all routeTypes
        RouteType.entries.forEach { routeType ->
            currentPage[routeType] = 1
            isLoadingMore[routeType] = false
            hasMoreData[routeType] = true
        }
    }

    fun onEvent(event: RoutesListEvent) {
        when (event) {
            is RoutesListEvent.UpdateSearch -> {
                _searchQuery.value = event.searchQuery
            }
            is RoutesListEvent.LoadRoutes -> {
                loadRoutes(event.routeType, event.page)
            }
            is RoutesListEvent.LoadMoreRoutes -> {
                loadMoreRoutes(event.routeType)
            }
        }
    }

    private fun loadRoutes(routeType: RouteType, page: Int = 1) {
        routeType.let { type ->
            currentPage[type] = page
            hasMoreData[type] = true

            viewModelScope.launch {
                repository.getRoutesList(_searchQuery.value, RouteType.toRouteStatus(type), page, perPage).collect { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _uiState.value = RoutesListState.Loading(type)
                        }
                        is Resource.Success -> {
                            if (result.data.isNullOrEmpty() || result.data.size < perPage) {
                                hasMoreData[type] = false
                            }
                            _uiState.value = RoutesListState.Success(
                                routes = result.data ?: emptyList(),
                                routeType = type,
                                isEmpty = result.data.isNullOrEmpty(),
                                isAppending = false
                            )
                        }
                        is Resource.Error -> {
                            _uiState.value = RoutesListState.Error(type, result.message ?: "Unknown error")
                        }
                    }
                }
            }
        }
    }

    private fun loadMoreRoutes(routeType: RouteType) {
        routeType.let { type ->
            if (isLoadingMore[type] == true
                || hasMoreData[type] == false
                || loadMoreJob?.isActive == true) {
                return
            }

            isLoadingMore[type] = true
            currentPage[type] = currentPage[type]?.plus(1) ?: 1

            loadMoreJob = viewModelScope.launch {
                try {
                    repository.getRoutesList(_searchQuery.value, RouteType.toRouteStatus(type), currentPage[type] ?: 1, perPage).collect { result ->
                        when (result) {
                            is Resource.Loading -> {
                                _uiState.value = RoutesListState.Loading(type)
                            }
                            is Resource.Success -> {
                                if (!result.waitForRemote && result.data.isNullOrEmpty()) {
                                    hasMoreData[type] = false
                                }
                                val currentData = (_uiState.value as? RoutesListState.Success)?.routes?.toMutableList() ?: mutableListOf()

                                result.data?.let { currentData.addAll(it) }
                                _uiState.value = RoutesListState.Success(
                                    routes = currentData,
                                    routeType = type,
                                    isEmpty = result.data.isNullOrEmpty(),
                                    isAppending = true
                                )
                            }
                            is Resource.Error -> {
                                _uiState.value = RoutesListState.Error(type, result.message ?: "Unknown error")
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

class RoutesViewModelFactory(private val repository: RouteRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RoutesViewModel::class.java)) {
            return RoutesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
