package com.carin.domain.models

import com.carin.domain.enums.RouteStatus

data class RouteUpdateModel(
    val id: String,
    val userId: Int?,
    val vehicleId: String?,
    val startDate: String?,
    val status: RouteStatus?,
    val avoidTolls: Boolean?,
    val avoidHighways: Boolean?
)
