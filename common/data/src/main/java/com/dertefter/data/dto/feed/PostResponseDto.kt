package com.dertefter.data.dto.feed

import kotlinx.serialization.Serializable

@Serializable
data class PostResponseDto(
    val data: PostDto
)