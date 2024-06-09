package com.carin.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LocationRequest(
    @SerializedName("city") val city: String,
    @SerializedName("country") val country: String
)
