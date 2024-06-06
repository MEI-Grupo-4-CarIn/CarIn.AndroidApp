package com.carin.data.remote.dto.auth

import com.google.gson.annotations.SerializedName

data class AuthRegisterRequest(
    @SerializedName("firstName")val firstName: String,
    @SerializedName("lastName")val lastName: String,
    @SerializedName("email")val email: String,
    @SerializedName("password")val password: String,
    @SerializedName("birthDate")val birthDate: String
)
