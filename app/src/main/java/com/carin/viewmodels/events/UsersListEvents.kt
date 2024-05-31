package com.carin.viewmodels.events

import com.carin.domain.enums.Role

sealed class UsersListEvent {
        data class UpdateSearch(val searchQuery: String?) : UsersListEvent()
        data class LoadUsers(val role: Role?, val page: Int = 1) : UsersListEvent()
        data class LoadMoreUsers(val role: Role?) : UsersListEvent()

}