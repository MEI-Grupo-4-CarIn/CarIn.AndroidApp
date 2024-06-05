package com.carin.data.local

import androidx.room.TypeConverter
import com.carin.domain.enums.FuelType
import com.carin.domain.enums.Role
import com.carin.domain.enums.RouteStatus
import com.carin.domain.enums.VehicleStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Converters {
    @TypeConverter
    fun roleToInt(role: Role?): Int? {
        return role?.roleId
    }

    @TypeConverter
    fun intToRole(roleId: Int?): Role? {
        return roleId?.let { Role.fromId(it) }
    }

    @TypeConverter
    fun routeStatusToInt(routeStatus: RouteStatus?): Int? {
        return routeStatus?.statusId
    }

    @TypeConverter
    fun intToRouteStatus(statusId: Int?): RouteStatus? {
        return statusId?.let { RouteStatus.fromId(it) }
    }

    @TypeConverter
    fun stringToDate(value: String?): Date? {
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

    @TypeConverter
    fun stringToDoubles(value: String?): List<Double>? {
        return value?.split(",")?.map { it.toDouble() }
    }

    @TypeConverter
    fun doublesToString(list: List<Double>?): String? {
        return list?.joinToString(",")
    }

    @TypeConverter
    fun fuelTypeToInt(fuelType: FuelType?): Int? {
        return fuelType?.fuelTypeId
    }

    @TypeConverter
    fun intToFuelType(fuelTypeId: Int?): FuelType? {
        return fuelTypeId?.let { FuelType.fromId(it) }
    }

    @TypeConverter
    fun vehicleStatusToInt(vehicleStatus: VehicleStatus?): Int? {
        return vehicleStatus?.statusId
    }

    @TypeConverter
    fun intToVehicleStatus(statusId: Int?): VehicleStatus? {
        return statusId?.let { VehicleStatus.fromId(it) }
    }
}