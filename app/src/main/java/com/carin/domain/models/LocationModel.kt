package com.carin.domain.models

data class LocationModel(
    val city: String,
    val country: String,
    val coordinates: List<Double>
)
