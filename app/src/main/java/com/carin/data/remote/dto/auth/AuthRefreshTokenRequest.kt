package com.carin.data.remote.dto.auth

import com.google.gson.annotations.SerializedName

data class AuthRefreshTokenRequest(
    @SerializedName("refreshToken")val refreshToken: String
)
