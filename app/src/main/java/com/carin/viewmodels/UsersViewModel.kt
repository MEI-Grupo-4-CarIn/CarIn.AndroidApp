package com.carin.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.carin.data.repositories.UserRepository
import com.carin.domain.enums.Role
import com.carin.utils.Resource
import com.carin.viewmodels.events.UsersListEvent
import com.carin.viewmodels.states.UsersListState
import kotlinx.coroutines.launch

class UsersViewModel(private val repository: UserRepository) : ViewModel() {

    private val _uiState = MutableLiveData<UsersListState>()
    val uiState: LiveData<UsersListState> get() = _uiState
    private val _searchQuery = MutableLiveData<String?>()
    val searchQuery: LiveData<String?> get() = _searchQuery

    private val perPage = 10
    private var currentPage = 1
    private var currentRole: Role? = null
    private var isLoadingMore = false
    private var hasMoreData = true

    fun onEvent(event: UsersListEvent) {
        when (event) {
            is UsersListEvent.UpdateSearch -> {
                _searchQuery.value = event.searchQuery
            }
            is UsersListEvent.LoadUsers -> {
                loadUsers(event.role, event.page)
            }
            is UsersListEvent.LoadMoreUsers -> {
                loadMoreUsers(event.role)
            }
        }
    }

    private fun loadUsers(role: Role?, page: Int = 1) {
        currentRole = role
        currentPage = page
        hasMoreData = true

        viewModelScope.launch {
            repository.getUsersList(_searchQuery.value, role, page, perPage).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.value = UsersListState.Loading(role)
                    }
                    is Resource.Success -> {
                        if (result.data.isNullOrEmpty()) {
                            hasMoreData = false
                        }
                        _uiState.value = UsersListState.Success(
                            users = result.data ?: emptyList(),
                            role = role,
                            isAppending = false
                        )
                    }
                    is Resource.Error -> {
                        _uiState.value = UsersListState.Error(result.message ?: "Unknown error")
                    }
                }
            }
        }
    }

    private fun loadMoreUsers(role: Role?) {
        if (isLoadingMore || !hasMoreData) return

        isLoadingMore = true
        currentPage++

        viewModelScope.launch {
            repository.getUsersList(_searchQuery.value, role, currentPage, perPage).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.value = UsersListState.Loading(role)
                    }
                    is Resource.Success -> {
                        if (result.data.isNullOrEmpty()) {
                            hasMoreData = false
                        }
                        val currentData = (_uiState.value as? UsersListState.Success)?.users?.toMutableList() ?: mutableListOf()

                        result.data?.let { currentData.addAll(it) }
                        _uiState.value = UsersListState.Success(
                            users = currentData,
                            role = role,
                            isAppending = true
                        )
                    }
                    is Resource.Error -> {
                        _uiState.value = UsersListState.Error(result.message ?: "Unknown error")
                    }
                }
                isLoadingMore = false
            }
        }
    }
}

class UsersViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UsersViewModel::class.java)) {
            return UsersViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
