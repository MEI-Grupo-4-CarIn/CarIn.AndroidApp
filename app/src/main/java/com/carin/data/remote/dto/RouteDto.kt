package com.carin.data.remote.dto

import com.google.gson.annotations.SerializedName
import java.util.Date

data class RouteDto(
    @SerializedName("_id")val id: String,
    @SerializedName("userId")val userId: Int,
    @SerializedName("vehicleId")val vehicleId: String,
    @SerializedName("startPoint")val startPoint: LocationDto,
    @SerializedName("endPoint")val endPoint: LocationDto,
    @SerializedName("startDate")val startDate: Date,
    @SerializedName("estimatedEndDate")val estimatedEndDate: Date,
    @SerializedName("distance")val distance: Double?,
    @SerializedName("duration")val duration: String?,
    @SerializedName("status")val status: String,
    @SerializedName("avoidTolls")val avoidTolls: Boolean,
    @SerializedName("avoidHighways")val avoidHighways: Boolean,
    @SerializedName("isDeleted")val isDeleted: Boolean,
    @SerializedName("createdAt")val creationDateUtc: Date,
    @SerializedName("updatedAt")val lastUpdateDateUtc: Date?
)
