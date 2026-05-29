package com.dertefter.data.dto.me

import kotlinx.serialization.Serializable

@Serializable
data class UpdateMeRequestDto(
    val displayName: String? = null,
    val username: String? = null,
    val bio: String? = null
)