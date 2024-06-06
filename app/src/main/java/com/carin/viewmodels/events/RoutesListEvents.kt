package com.carin.viewmodels.events

import com.carin.domain.enums.RouteType

sealed class RoutesListEvent {
        data class UpdateSearch(val searchQuery: String?) : RoutesListEvent()
        data class LoadRoutes(val routeType: RouteType, val page: Int = 1) : RoutesListEvent()
        data class LoadMoreRoutes(val routeType: RouteType) : RoutesListEvent()
}