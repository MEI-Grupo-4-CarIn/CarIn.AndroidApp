package com.carin.data.local.entities

import androidx.room.Entity

@Entity(tableName = "location")
data class LocationEntity(
    val city: String,
    val country: String,
    val coordinates: List<Double>
)
