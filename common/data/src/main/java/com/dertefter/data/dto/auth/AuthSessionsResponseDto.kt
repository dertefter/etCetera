package com.dertefter.data.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthSessionsResponseDto(
    val sessions: List<AuthSessionDto>
)
