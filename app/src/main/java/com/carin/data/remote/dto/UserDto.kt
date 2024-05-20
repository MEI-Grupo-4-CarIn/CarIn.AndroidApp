package com.carin.data.remote.dto

import com.carin.domain.enums.Role
import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("userId")val id: Int,
    @SerializedName("firstName")val firstName: String,
    @SerializedName("lastName")val lastName: String,
    @SerializedName("email")val email: String,
    @SerializedName("birthDate")val birthDate: String,
    @SerializedName("roleId")val roleId: Role,
    @SerializedName("status")val status: Boolean,
    @SerializedName("createdAt")val createdAt: String,
    @SerializedName("updatedAt")val updatedAt: String
)
