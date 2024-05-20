package com.carin.domain.models

import com.carin.domain.enums.Role

data class UserInfo(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val birthDate: String,
    val role: Role,
    val status: Boolean,
    val createdAt: String,
    val updatedAt: String
)
