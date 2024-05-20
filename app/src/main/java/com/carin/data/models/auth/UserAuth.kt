package com.carin.data.models.auth

data class UserAuth(
    val userId: Int,
    val email: String,
    val firstName: String,
    val lastName: String,
    val token: String,
    val refreshToken: String,
    val expiresIn: Int
)

