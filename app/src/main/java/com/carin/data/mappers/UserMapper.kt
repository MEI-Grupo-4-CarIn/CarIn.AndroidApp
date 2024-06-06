package com.carin.data.mappers

import com.carin.R
import com.carin.data.local.entities.UserEntity
import com.carin.data.remote.dto.UserDto
import com.carin.data.remote.dto.auth.AuthRegisterDto
import com.carin.data.remote.dto.auth.AuthRegisterRequest
import com.carin.domain.models.UserModel
import com.carin.domain.models.UserRegisterModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun UserEntity.toUserModel(): UserModel {
    return UserModel(
        id = id,
        imageResource = R.drawable.ic_person_blue,
        firstName = firstName,
        lastName = lastName,
        email = email,
        birthDate = birthDate,
        role = roleId,
        status = status,
        creationDateUtc = creationDateUtc,
        lastUpdateDateUtc = lastUpdateDateUtc
    )
}

fun UserDto.toUserEntity(): UserEntity {
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    return UserEntity(
        id = id,
        firstName = firstName,
        lastName = lastName,
        email = email,
        birthDate = dateFormatter.parse(birthDate) ?: Date(),
        roleId = role,
        status = status,
        creationDateUtc = dateFormatter.parse(creationDateUtc) ?: Date(),
        lastUpdateDateUtc = lastUpdateDateUtc?.let { dateFormatter.parse(it) },
        localLastUpdateDateUtc = Date()
    )
}

fun UserRegisterModel.toAuthRegisterRequest(): AuthRegisterRequest {
    return AuthRegisterRequest(
        firstName = firstName,
        lastName = lastName,
        email = email,
        password = password,
        birthDate = birthDate
    )
}

fun AuthRegisterDto.toUserEntity(): UserEntity {
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    return UserEntity(
        id = id,
        firstName = firstName,
        lastName = lastName,
        email = email,
        birthDate = dateFormatter.parse(birthDate) ?: Date(),
        roleId = role,
        status = status,
        creationDateUtc = dateFormatter.parse(creationDateUtc) ?: Date(),
        lastUpdateDateUtc = lastUpdateDateUtc?.let { dateFormatter.parse(it) },
        localLastUpdateDateUtc = Date()
    )
}
