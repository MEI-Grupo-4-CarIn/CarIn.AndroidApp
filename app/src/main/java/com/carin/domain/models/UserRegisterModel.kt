package com.carin.domain.models

data class UserRegisterModel(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val birthDate: String
)