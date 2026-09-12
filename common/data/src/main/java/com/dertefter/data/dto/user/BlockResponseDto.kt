package com.dertefter.data.dto.user

import kotlinx.serialization.Serializable

@Serializable
data class BlockResponseDto(
    val blocked: Boolean
)
