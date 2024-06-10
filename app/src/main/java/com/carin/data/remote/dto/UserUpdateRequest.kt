package com.carin.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UserUpdateRequest(
    @SerializedName("firstName") val firstName: String?,
    @SerializedName("lastName") val lastName: String?,
    @SerializedName("email") val email: String?
)