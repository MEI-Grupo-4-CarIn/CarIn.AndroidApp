package com.carin.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.carin.domain.enums.Role

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val birthDate: String,
    val roleId: Int,
    val status: Boolean,
    val createdAt: String,
    val updatedAt: String
)
