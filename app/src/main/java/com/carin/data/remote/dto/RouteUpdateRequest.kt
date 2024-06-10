package com.carin.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RouteUpdateRequest(
    @SerializedName("userId") val userId: String?,
    @SerializedName("vehicleId") val vehicleId: String?,
    @SerializedName("startDate") val startDate: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("avoidTolls") val avoidTolls: Boolean?,
    @SerializedName("avoidHighways") val avoidHighways: Boolean?
)
