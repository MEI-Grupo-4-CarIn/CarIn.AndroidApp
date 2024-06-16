package com.carin.viewmodels.events

import com.carin.domain.enums.RouteType
import com.carin.domain.models.UserAuth

sealed class RoutesListEvent {
        data class UpdateSearch(val searchQuery: String?) : RoutesListEvent()
        data class LoadRoutes(val routeType: RouteType, val page: Int = 1, val userAuth: UserAuth? = null) : RoutesListEvent()
        data class LoadMoreRoutes(val routeType: RouteType, val userAuth: UserAuth? = null) : RoutesListEvent()
}