package com.carin.data.models.response

import com.google.gson.annotations.SerializedName

data class AuthLoginResponse(
    @SerializedName("token") val token: String,
    @SerializedName("refreshToken") val refreshToken: String,
    @SerializedName("expiresIn") val expiresIn: Int
)