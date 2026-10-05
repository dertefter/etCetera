package com.dertefter.data.dto.me

import kotlinx.serialization.Serializable

@Serializable
data class UpdateMeResponse(
    val id: String,
    val username: String,
    val displayName: String,
    val bio: String? = "",
    val updatedAt: String,
)

