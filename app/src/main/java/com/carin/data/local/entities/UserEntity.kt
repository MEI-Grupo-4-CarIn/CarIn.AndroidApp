package com.carin.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.carin.domain.enums.Role
import java.util.Date

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = false) val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val birthDate: Date,
    val roleId: Role,
    val status: Boolean,
    val creationDateUtc: Date,
    val lastUpdateDateUtc: Date?,
    val localLastUpdateDateUtc: Date
)
