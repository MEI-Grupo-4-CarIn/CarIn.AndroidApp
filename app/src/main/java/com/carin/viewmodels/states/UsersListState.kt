package com.carin.viewmodels.states

import com.carin.domain.enums.UserType
import com.carin.domain.models.UserModel

sealed class UsersListState {
    data class Loading(val userType: UserType, ) : UsersListState()
    data class Success(
        val users: List<UserModel>,
        val userType: UserType,
        val isAppending: Boolean = false
    ) : UsersListState()
    data class Error(val message: String) : UsersListState()
}
