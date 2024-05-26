package com.carin.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.carin.R
import com.carin.domain.enums.Role
import com.carin.domain.models.UserModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = false) val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val birthDate: Date,
    val roleId: Int,
    val status: Boolean,
    val creationDateUtc: Date,
    val lastUpdateDateUtc: Date?,
    val localLastUpdateDateUtc: Date
)
