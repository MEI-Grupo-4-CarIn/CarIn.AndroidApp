package com.carin.data.mappers

import com.carin.data.local.entities.RouteEntity
import com.carin.data.local.entities.RouteWithInfoEntity
import com.carin.data.remote.dto.RouteDto
import com.carin.domain.enums.RouteStatus
import com.carin.domain.models.RouteModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun RouteWithInfoEntity.toRouteModel(): RouteModel {
    return RouteModel(
        id = route.id,
        userId = route.userId,
        vehicleId = route.vehicleId,
        startPoint = route.startPoint.toLocationModel(),
        endPoint = route.endPoint.toLocationModel(),
        startDate = route.startDate,
        estimatedEndDate = route.estimatedEndDate,
        distance = route.distance,
        duration = route.duration,
        status = route.status,
        avoidTolls = route.avoidTolls,
        avoidHighways = route.avoidHighways,
        isDeleted = route.isDeleted,
        creationDateUtc = route.creationDateUtc,
        lastUpdateDateUtc = route.lastUpdateDateUtc,
        user = user.toUserModel(),
    )
}

fun RouteDto.toRouteEntity(): RouteEntity {
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    return RouteEntity(
        id = id,
        userId = userId,
        vehicleId = vehicleId,
        startPoint = startPoint.toLocationEntity(),
        endPoint = endPoint.toLocationEntity(),
        startDate = startDate,
        estimatedEndDate = estimatedEndDate,
        distance = distance,
        duration = duration,
        status = RouteStatus.fromDescription(status) ?: RouteStatus.Pending,
        avoidTolls = avoidTolls,
        avoidHighways = avoidHighways,
        isDeleted = isDeleted,
        creationDateUtc = creationDateUtc,
        lastUpdateDateUtc = lastUpdateDateUtc,
        localLastUpdateDateUtc = Date()
    )
}