package com.carin.domain.models

import com.carin.domain.enums.RouteStatus

data class RouteCreationModel(
    val userId: Int,
    val vehicleId: String,
    val startPoint: LocationCreationModel,
    val endPoint: LocationCreationModel,
    val startDate: String,
    val status: RouteStatus = RouteStatus.Pending,
    val avoidTolls: Boolean,
    val avoidHighways: Boolean
)
