package com.carin.domain.models

data class UserUpdateModel(
    val id: Int,
    val firstName: String?,
    val lastName: String?,
    val email: String?
)
