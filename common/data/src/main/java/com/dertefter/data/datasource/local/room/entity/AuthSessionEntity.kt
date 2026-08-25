package com.dertefter.data.datasource.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dertefter.data.dto.auth.AuthSessionDto

@Entity(tableName = "auth_sessions")
data class AuthSessionEntity(
    @PrimaryKey val id: String,
    val isCurrent: Boolean,
    val createdAt: String,
    val lastUsedAt: String,
    val expiresAt: String,
    val ipAddress: String,
    val ipCountry: String?,
    val ipCity: String?,
    val deviceType: String?,
    val osName: String?,
    val osVersion: String?,
    val clientName: String?,
    val clientVersion: String?,
    val deviceModel: String?
)

fun AuthSessionEntity.asExternalModel() = AuthSessionDto(
    id = id,
    isCurrent = isCurrent,
    createdAt = createdAt,
    lastUsedAt = lastUsedAt,
    expiresAt = expiresAt,
    ipAddress = ipAddress,
    ipCountry = ipCountry,
    ipCity = ipCity,
    deviceType = deviceType,
    osName = osName,
    osVersion = osVersion,
    clientName = clientName,
    clientVersion = clientVersion,
    deviceModel = deviceModel
)

fun AuthSessionDto.asEntity() = AuthSessionEntity(
    id = id,
    isCurrent = isCurrent,
    createdAt = createdAt,
    lastUsedAt = lastUsedAt,
    expiresAt = expiresAt,
    ipAddress = ipAddress,
    ipCountry = ipCountry,
    ipCity = ipCity,
    deviceType = deviceType,
    osName = osName,
    osVersion = osVersion,
    clientName = clientName,
    clientVersion = clientVersion,
    deviceModel = deviceModel
)
