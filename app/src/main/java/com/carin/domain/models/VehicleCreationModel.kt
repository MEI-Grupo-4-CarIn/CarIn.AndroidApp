package com.carin.domain.models

import com.carin.domain.enums.FuelType
import com.carin.domain.enums.VehicleStatus

data class VehicleCreationModel(
    val model: String,
    val brand: String,
    val licensePlate: String,
    val vin: String,
    val color: String,
    val registerDate: String,
    val acquisitionDate: String,
    val category: String,
    val kms: Double,
    val capacity: Double,
    val fuelType: FuelType,
    val averageFuelConsumption: Double,
    val status: VehicleStatus = VehicleStatus.None
)
