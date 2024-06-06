package com.carin.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LocationDto(
    @SerializedName("city")val city: String,
    @SerializedName("country")val country: String,
    @SerializedName("coordinates")val coordinates: List<Double>
)
