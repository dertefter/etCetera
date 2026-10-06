package com.dertefter.data.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthSessionDto(
    val id: String,
    val isCurrent: Boolean,
    val createdAt: String,
    val lastUsedAt: String,
    val expiresAt: String,
    val ipAddress: String,
    val ipCountry: String? = null,
    val ipCity: String? = null,
    val deviceType: String? = null,
    val osName: String? = null,
    val osVersion: String? = null,
    val clientName: String? = null,
    val clientVersion: String? = null,
    val deviceModel: String? = null
)
