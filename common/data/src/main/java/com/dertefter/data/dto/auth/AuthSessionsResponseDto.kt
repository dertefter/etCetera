package com.dertefter.data.dto.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthSessionsResponseDto(
    @SerialName("sessions") val sessions: List<AuthSessionDto>
)
