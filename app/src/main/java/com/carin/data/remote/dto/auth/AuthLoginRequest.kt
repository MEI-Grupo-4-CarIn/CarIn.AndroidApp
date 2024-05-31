package com.carin.data.remote.dto.auth

import com.google.gson.annotations.SerializedName

data class AuthLoginRequest(
    @SerializedName("email")val email: String,
    @SerializedName("password")val password: String
)
