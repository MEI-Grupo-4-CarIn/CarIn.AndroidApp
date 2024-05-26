package com.carin.domain.models

import com.carin.domain.enums.Role
import java.util.Date

data class UserModel(
    val id: Int,
    val imageResource: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val birthDate: Date,
    val role: Role,
    val status: Boolean,
    val creationDateUtc: Date,
    val lastUpdateDateUtc: Date?
)
