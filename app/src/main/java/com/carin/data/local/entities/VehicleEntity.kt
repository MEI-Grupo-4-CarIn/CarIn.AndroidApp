package com.carin.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.carin.domain.enums.FuelType
import com.carin.domain.enums.VehicleStatus
import java.util.Date

@Entity(tableName = "vehicles")
data class VehicleEntity(
    @PrimaryKey(autoGenerate = false) val id: String,
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
    val localLastUpdateDateUtc: Date
)
