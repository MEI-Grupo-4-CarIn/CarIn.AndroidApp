package com.carin.data.mappers

import com.carin.data.local.entities.LocationEntity
import com.carin.data.remote.dto.LocationDto
import com.carin.data.remote.dto.LocationRequest
import com.carin.domain.models.LocationCreationModel
import com.carin.domain.models.LocationModel

fun LocationEntity.toLocationModel(): LocationModel {
    return LocationModel(
        city = city,
        country = country,
        coordinates = coordinates
    )
}

fun LocationDto.toLocationEntity(): LocationEntity {
    return LocationEntity(
        city = city,
        country = country,
        coordinates = coordinates
    )
}

fun LocationCreationModel.toLocationRequest(): LocationRequest {
    return LocationRequest(
        city = city,
        country = country
    )
}