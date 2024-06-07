package com.carin.domain.models

import com.carin.domain.enums.RouteStatus
import java.util.Date

data class RouteModel(
    val id: String,
    val userId: Int,
    val vehicleId: String,
    val startPoint: LocationModel,
    val endPoint: LocationModel,
    val startDate: Date,
    val estimatedEndDate: Date,
    val distance: Double?,
    val duration: String?,
    val status: RouteStatus,
    val avoidTolls: Boolean,
    val avoidHighways: Boolean,
    val isDeleted: Boolean,
    val creationDateUtc: Date,
    val lastUpdateDateUtc: Date?,
    val user: UserModel?,
    val vehicle: VehicleModel?
)
