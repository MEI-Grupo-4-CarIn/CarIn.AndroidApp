package com.carin.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.carin.data.repositories.UserRepository
import com.carin.domain.enums.UserType
import com.carin.utils.Resource
import com.carin.viewmodels.events.UsersListEvent
import com.carin.viewmodels.states.UsersListState
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class UsersViewModel(private val repository: UserRepository) : ViewModel() {

    private val _uiState = MutableLiveData<UsersListState>()
    val uiState: LiveData<UsersListState> get() = _uiState
    private val _searchQuery = MutableLiveData<String?>()
    val searchQuery: LiveData<String?> get() = _searchQuery

    private val perPage = 50
    private var currentPage = mutableMapOf<UserType, Int>()
    private var isLoadingMore = mutableMapOf<UserType, Boolean>()
    private var hasMoreData = mutableMapOf<UserType, Boolean>()

    private var loadMoreJob: Job? = null

    init {
        // Initialize default values for all userTypes
        UserType.entries.forEach { userType ->
            currentPage[userType] = 1
            isLoadingMore[userType] = false
            hasMoreData[userType] = true
        }
    }

    fun onEvent(event: UsersListEvent) {
        when (event) {
            is UsersListEvent.UpdateSearch -> {
                _searchQuery.value = event.searchQuery
            }
            is UsersListEvent.LoadUsers -> {
                loadUsers(event.userType, event.page)
            }
            is UsersListEvent.LoadMoreUsers -> {
                loadMoreUsers(event.userType)
            }
        }
    }

    private fun loadUsers(userType: UserType, page: Int = 1) {
        userType.let { type ->
            currentPage[type] = page
            hasMoreData[type] = true

            viewModelScope.launch {
                repository.getUsersList(_searchQuery.value, UserType.toRole(type), page, perPage).collect { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _uiState.value = UsersListState.Loading(type)
                        }
                        is Resource.Success -> {
                            if (!result.waitForRemote && (result.data.isNullOrEmpty()  || result.data.size < perPage)) {
                                hasMoreData[type] = false
                            }
                            _uiState.value = UsersListState.Success(
                                users = result.data ?: emptyList(),
                                userType = type,
                                isEmpty = result.data.isNullOrEmpty(),
                                isAppending = false
                            )
                        }
                        is Resource.Error -> {
                            _uiState.value = UsersListState.Error(type, result.message ?: "Unknown error")
                        }
                    }
                }
            }
        }
    }

    private fun loadMoreUsers(userType: UserType) {
        userType.let { type ->
            if (isLoadingMore[type] == true
                || hasMoreData[type] == false
                || loadMoreJob?.isActive == true) {
                return
            }

            isLoadingMore[type] = true
            currentPage[type] = currentPage[type]?.plus(1) ?: 1

            loadMoreJob = viewModelScope.launch {
                try {
                    repository.getUsersList(_searchQuery.value, UserType.toRole(type), currentPage[type] ?: 1, perPage).collect { result ->
                        when (result) {
                            is Resource.Loading -> {
                                _uiState.value = UsersListState.Loading(type)
                            }
                            is Resource.Success -> {
                                if (!result.waitForRemote && result.data.isNullOrEmpty()) {
                                    hasMoreData[type] = false
                                }
                                val currentData = (_uiState.value as? UsersListState.Success)?.users?.toMutableList() ?: mutableListOf()

                                result.data?.let { currentData.addAll(it) }
                                _uiState.value = UsersListState.Success(
                                    users = currentData,
                                    userType = type,
                                    isEmpty = result.data.isNullOrEmpty(),
                                    isAppending = true
                                )
                            }
                            is Resource.Error -> {
                                _uiState.value = UsersListState.Error(type, result.message ?: "Unknown error")
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

class UsersViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UsersViewModel::class.java)) {
            return UsersViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
