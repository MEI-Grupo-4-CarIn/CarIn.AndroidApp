package com.carin.data.local.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.carin.domain.enums.RouteStatus
import java.util.Date

@Entity(
    tableName = "routes",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.RESTRICT,
        )
    ],
    indices = [
        Index(value = ["userId"]),
        Index(value = ["vehicleId"]),
        Index(value = ["status"]),
        Index(value = ["isDeleted"]),
        Index(value = ["startPoint_city", "startPoint_country", "endPoint_city", "endPoint_country"])
    ]
)
data class RouteEntity(
    @PrimaryKey(autoGenerate = false) val id: String,
    val userId: Int,
    val vehicleId: String,
    @Embedded(prefix = "startPoint_") val startPoint: LocationEntity,
    @Embedded(prefix = "endPoint_") val endPoint: LocationEntity,
    val startDate: Date,
    val estimatedEndDate: Date,
    val distance: Double?,
    val duration: String?,
    val status: RouteStatus,
    val avoidTolls: Boolean,
    val avoidHighways: Boolean,
    val isDeleted: Boolean,
    val creationDateUtc: Date,
    val lastUpdateDateUtc: Date?,
    val localLastUpdateDateUtc: Date
)
