package com.carin.data.remote.dto

import com.google.gson.annotations.SerializedName
import java.util.Date

data class VehicleDto(
    @SerializedName("_id")val id: String,
    @SerializedName("model")val model: String,
    @SerializedName("brand")val brand: String,
    @SerializedName("licensePlate")val licensePlate: String,
    @SerializedName("vin")val vin: String,
    @SerializedName("color")val color: String,
    @SerializedName("registerDate")val registerDateUtc: Date,
    @SerializedName("acquisitionDate")val acquisitionDateUtc: Date,
    @SerializedName("category")val category: String,
    @SerializedName("kms")val kms: Double,
    @SerializedName("capacity")val capacity: Double,
    @SerializedName("fuelType")val fuelType: String,
    @SerializedName("averageFuelConsumption")val averageFuelConsumption: Double,
    @SerializedName("status")val status: String,
    @SerializedName("isDeleted")val isDeleted: Boolean,
    @SerializedName("createdAt")val creationDateUtc: Date,
    @SerializedName("updatedAt")val lastUpdateDateUtc: Date
)