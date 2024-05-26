package com.carin.data.models.auth

import com.carin.domain.enums.Role
import java.util.Date

data class UserAuth(
    val userId: Int,
    val email: String,
    val firstName: String,
    val lastName: String,
    val role: Role,
    val token: String,
    val refreshToken: String,
    val expiresOn: Date
)

