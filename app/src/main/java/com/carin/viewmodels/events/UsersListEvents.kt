package com.carin.viewmodels.events

import com.carin.domain.enums.UserType

sealed class UsersListEvent {
        data class UpdateSearch(val searchQuery: String?) : UsersListEvent()
        data class LoadUsers(val userType: UserType, val page: Int = 1) : UsersListEvent()
        data class LoadMoreUsers(val userType: UserType) : UsersListEvent()
}