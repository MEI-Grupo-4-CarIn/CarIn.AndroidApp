package com.carin.domain.models

import com.carin.domain.enums.FuelType
import com.carin.domain.enums.VehicleStatus
import java.util.Date

data class VehicleModel(
    val id: String,
    val imageResource: Int,
    val model: String,
    val brand: String,
    val licensePlate: String,
    val vin: String,
    val color: String,
    val registerDate: Date,
    val acquisitionDate: Date,
    val category: String,
    val kms: Double,
    val capacity: Double,
    val fuelType: FuelType,
    val averageFuelConsumption: Double,
    val status: VehicleStatus,
    val isDeleted: Boolean,
    val creationDateUtc: Date,
    val lastUpdateDateUtc: Date?,
)
