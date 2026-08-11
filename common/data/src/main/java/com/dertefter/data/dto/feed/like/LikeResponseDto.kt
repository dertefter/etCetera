package com.dertefter.data.dto.feed.like

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LikeResponseDto(
    @SerialName("liked")
    val liked: Boolean,

    @SerialName("likesCount")
    val likesCount: Int
)

