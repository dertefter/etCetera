package com.dertefter.data.dto.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ActiveNicknameDto(
    @SerialName("id") val id: String? = null,
    @SerialName("label") val label: String? = null,
    @SerialName("styleKey") val styleKey: String? = null,
    @SerialName("eventId") val eventId: String? = null,
    @SerialName("expiresAt") val expiresAt: String? = null,
    @SerialName("stateVersion") val stateVersion: Int? = null
)
