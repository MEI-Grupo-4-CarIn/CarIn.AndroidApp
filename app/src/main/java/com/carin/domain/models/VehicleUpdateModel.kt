package com.carin.domain.models

import com.carin.domain.enums.VehicleStatus

data class VehicleUpdateModel(
    val id: String,
    val color: String?,
    val kms: Double?,
    val averageFuelConsumption: Double?,
    val status: VehicleStatus?
)
