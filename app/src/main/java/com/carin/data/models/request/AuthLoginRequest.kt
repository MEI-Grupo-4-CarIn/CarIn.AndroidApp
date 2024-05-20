package com.carin.data.models.request

import com.google.gson.annotations.SerializedName

data class AuthLoginRequest(
    @SerializedName("email")val email: String,
    @SerializedName("password")val password: String
)
