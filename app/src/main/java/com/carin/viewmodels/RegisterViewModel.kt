package com.carin.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.carin.data.repositories.UserRepository
import com.carin.domain.models.UserRegisterModel
import com.carin.utils.Resource
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: UserRepository) : ViewModel() {

    private val _registrationState = MutableLiveData<Resource<Boolean>>()
    val registrationState: LiveData<Resource<Boolean>> get() = _registrationState

    fun registerUser(userRegisterModel: UserRegisterModel) {
        viewModelScope.launch {
            repository.registerUser(userRegisterModel).collect { result ->
                _registrationState.value = result
            }
        }
    }
}

class RegisterViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}