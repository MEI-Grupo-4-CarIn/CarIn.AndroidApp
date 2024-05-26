package com.carin.data.local

import androidx.room.TypeConverter
import com.carin.domain.enums.Role
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Converters {
    @TypeConverter
    fun fromRole(role: Role?): Int? {
        return role?.roleId
    }

    @TypeConverter
    fun toRole(roleId: Int?): Role? {
        return roleId?.let { Role.fromId(it) }
    }

    @TypeConverter
    fun fromString(value: String?): Date? {
        return value?.let {
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            format.parse(it)
        }
    }

    @TypeConverter
    fun dateToString(date: Date?): String? {
        return date?.let {
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            format.format(it)
        }
    }
}