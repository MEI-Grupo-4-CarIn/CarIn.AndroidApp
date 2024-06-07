package com.carin.viewmodels.states

import com.carin.domain.enums.RouteType
import com.carin.domain.models.RouteModel

sealed class RoutesListState {
    data class Loading(val routeType: RouteType? = null) : RoutesListState()
    data class Success(
        val routes: List<RouteModel>,
        val routeType: RouteType? = null,
        val isEmpty: Boolean = false,
        val isAppending: Boolean = false
    ) : RoutesListState()
    data class Error(
        val message: String,
        val routeType: RouteType? = null
    ) : RoutesListState()
}
