package com.dertefter.data.dto.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthSessionDto(
    @SerialName("id") val id: String,
    @SerialName("isCurrent") val isCurrent: Boolean,
    @SerialName("createdAt") val createdAt: String,
    @SerialName("lastUsedAt") val lastUsedAt: String,
    @SerialName("expiresAt") val expiresAt: String,
    @SerialName("ipAddress") val ipAddress: String,
    @SerialName("ipCountry") val ipCountry: String? = null,
    @SerialName("ipCity") val ipCity: String? = null,
    @SerialName("deviceType") val deviceType: String? = null,
    @SerialName("osName") val osName: String? = null,
    @SerialName("osVersion") val osVersion: String? = null,
    @SerialName("clientName") val clientName: String? = null,
    @SerialName("clientVersion") val clientVersion: String? = null,
    @SerialName("deviceModel") val deviceModel: String? = null
)
