package com.dertefter.data.dto.event

import kotlinx.serialization.Serializable

@Serializable
data class ActiveNicknameDto(
    val id: String? = null,
    val label: String? = null,
    val styleKey: String? = null,
    val eventId: String? = null,
    val expiresAt: String? = null,
    val stateVersion: Int? = null
)
