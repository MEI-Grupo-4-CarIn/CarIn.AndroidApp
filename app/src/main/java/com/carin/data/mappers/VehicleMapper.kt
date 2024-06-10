package com.carin.data.mappers

import com.carin.R
import com.carin.data.local.entities.VehicleEntity
import com.carin.data.remote.dto.VehicleCreationRequest
import com.carin.data.remote.dto.VehicleDto
import com.carin.domain.enums.FuelType
import com.carin.domain.enums.VehicleStatus
import com.carin.domain.models.VehicleCreationModel
import com.carin.domain.models.VehicleModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun VehicleEntity.toVehicleModel(): VehicleModel {
    return VehicleModel(
        id = id,
        imageResource = R.drawable.ic_vehicle_blue,
        model = model,
        brand =  brand,
        licensePlate = licensePlate,
        vin = vin,
        color = color,
        registerDate = registerDate,
        acquisitionDate = acquisitionDate,
        category = category,
        kms = kms,
        capacity = capacity,
        fuelType = fuelType,
        averageFuelConsumption = averageFuelConsumption,
        status = status,
        isDeleted = isDeleted,
        creationDateUtc = creationDateUtc,
        lastUpdateDateUtc = lastUpdateDateUtc,
    )
}

fun VehicleDto.toVehicleEntity(): VehicleEntity {
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    return VehicleEntity(
        id = id,
        model = model,
        brand =  brand,
        licensePlate = licensePlate,
        vin = vin,
        color = color,
        registerDate = registerDateUtc,
        acquisitionDate = acquisitionDateUtc,
        category = category,
        kms = kms,
        capacity = capacity,
        fuelType = FuelType.fromDescription(fuelType) ?: FuelType.Petrol,
        averageFuelConsumption = averageFuelConsumption,
        status = VehicleStatus.fromDescription(status) ?: VehicleStatus.None,
        isDeleted = isDeleted,
        creationDateUtc = creationDateUtc,
        lastUpdateDateUtc = lastUpdateDateUtc,
        localLastUpdateDateUtc = Date()
    )
}

fun VehicleCreationModel.toVehicleCreationRequest(): VehicleCreationRequest {
    return VehicleCreationRequest(
        model = model,
        brand = brand,
        licensePlate = licensePlate,
        vin = vin,
        color = color,
        registerDate = registerDate,
        acquisitionDate = acquisitionDate,
        category = category,
        kms = kms,
        capacity = capacity,
        fuelType = fuelType.toString().lowercase(),
        averageFuelConsumption = averageFuelConsumption,
        status = status.toString().lowercase()
    )
}
