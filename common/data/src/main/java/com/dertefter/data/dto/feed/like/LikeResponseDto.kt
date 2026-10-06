package com.dertefter.data.dto.feed.like

import kotlinx.serialization.Serializable

@Serializable
data class LikeResponseDto(
    val liked: Boolean,
    val likesCount: Int
)

