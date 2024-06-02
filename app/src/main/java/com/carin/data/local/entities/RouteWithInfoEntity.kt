package com.carin.data.local.entities

import androidx.room.Embedded
import androidx.room.Relation

data class RouteWithInfoEntity(
    @Embedded val route: RouteEntity,

    @Relation(
        parentColumn = "userId",
        entityColumn = "id"
    )
    val user: UserEntity
)