package com.carin.viewmodels.states

import com.carin.domain.enums.Role
import com.carin.domain.models.UserModel

sealed class UsersListState {
    data class Loading(
        val role: Role? = null,
    ) : UsersListState()
    data class Success(
        val users: List<UserModel>,
        val role: Role?,
        val isAppending: Boolean = false
    ) : UsersListState()
    data class Error(val message: String) : UsersListState()
}
