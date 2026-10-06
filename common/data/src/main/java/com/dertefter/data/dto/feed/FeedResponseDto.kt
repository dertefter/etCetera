package com.dertefter.data.dto.feed

import kotlinx.serialization.Serializable

@Serializable
data class FeedResponseDto(
    val data: PostDataDto
)