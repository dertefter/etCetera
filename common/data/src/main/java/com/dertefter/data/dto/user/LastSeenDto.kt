package com.dertefter.data.dto.user

import kotlinx.serialization.Serializable

@Serializable
data class LastSeenDto(
    val unit: String,
    val value: Int
)
