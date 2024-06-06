package com.carin.data.remote.dto.auth

import com.carin.domain.enums.Role
import com.google.gson.annotations.SerializedName

data class AuthRegisterDto(
    @SerializedName("userId") val id: Int,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("email") val email: String,
    @SerializedName("birthDate") val birthDate: String,
    @SerializedName("role") val role: Role,
    @SerializedName("status") val status: Boolean,
    @SerializedName("creationDateUtc")val creationDateUtc: String,
    @SerializedName("lastUpdateDateUtc")val lastUpdateDateUtc: String?
)
