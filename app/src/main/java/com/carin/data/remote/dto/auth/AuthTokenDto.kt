package com.carin.data.remote.dto.auth

import com.google.gson.annotations.SerializedName

data class AuthTokenDto(
    @SerializedName("token") val token: String,
    @SerializedName("refreshToken") val refreshToken: String,
    @SerializedName("expiresIn") val expiresIn: Int
)