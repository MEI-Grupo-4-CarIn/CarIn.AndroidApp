package com.carin.data.remote.dto

import com.google.gson.annotations.SerializedName

data class VehicleCreationRequest(
    @SerializedName("model") val model: String,
    @SerializedName("brand") val brand: String,
    @SerializedName("licensePlate") val licensePlate: String,
    @SerializedName("vin") val vin: String,
    @SerializedName("color") val color: String,
    @SerializedName("registerDate") val registerDate: String,
    @SerializedName("acquisitionDate") val acquisitionDate: String,
    @SerializedName("category") val category: String,
    @SerializedName("kms") val kms: Double,
    @SerializedName("capacity") val capacity: Double,
    @SerializedName("fuelType") val fuelType: String,
    @SerializedName("averageFuelConsumption") val averageFuelConsumption: Double,
    @SerializedName("status") val status: String,
)

