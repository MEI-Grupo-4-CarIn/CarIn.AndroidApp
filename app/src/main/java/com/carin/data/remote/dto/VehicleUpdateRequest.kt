package com.carin.data.remote.dto

import com.google.gson.annotations.SerializedName

data class VehicleUpdateRequest(
    @SerializedName("color") val color: String?,
    @SerializedName("kms") val kms: Double?,
    @SerializedName("averageFuelConsumption") val averageFuelConsumption: Double?,
    @SerializedName("status") val status: String?
)
