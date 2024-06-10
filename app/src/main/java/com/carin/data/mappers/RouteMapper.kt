package com.carin.data.mappers

import com.carin.data.local.entities.RouteEntity
import com.carin.data.local.entities.RouteWithInfoEntity
import com.carin.data.remote.dto.RouteCreationRequest
import com.carin.data.remote.dto.RouteDto
import com.carin.data.remote.dto.RouteUpdateRequest
import com.carin.domain.enums.RouteStatus
import com.carin.domain.models.RouteCreationModel
import com.carin.domain.models.RouteModel
import com.carin.domain.models.RouteUpdateModel
import java.util.Date

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
        vehicle = vehicle.toVehicleModel()
    )
}

fun RouteDto.toRouteEntity(): RouteEntity {
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
        status = RouteStatus.fromDescription(status)!!,
        avoidTolls = avoidTolls,
        avoidHighways = avoidHighways,
        isDeleted = isDeleted,
        creationDateUtc = creationDateUtc,
        lastUpdateDateUtc = lastUpdateDateUtc,
        localLastUpdateDateUtc = Date()
    )
}

fun RouteCreationModel.toRouteCreationRequest(): RouteCreationRequest {
    return RouteCreationRequest(
        userId = userId.toString(),
        vehicleId = vehicleId,
        startPoint = startPoint.toLocationRequest(),
        endPoint = endPoint.toLocationRequest(),
        startDate = startDate,
        status = status.externalKey,
        avoidTolls = avoidTolls,
        avoidHighways = avoidHighways
    )
}

fun RouteUpdateModel.toRouteUpdateRequest(): RouteUpdateRequest {
    return RouteUpdateRequest(
        userId = userId?.toString(),
        vehicleId = vehicleId,
        startDate = startDate,
        status = status?.externalKey,
        avoidTolls = avoidTolls,
        avoidHighways = avoidHighways
    )
}