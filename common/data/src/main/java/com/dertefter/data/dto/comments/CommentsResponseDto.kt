package com.dertefter.data.dto.comments

import kotlinx.serialization.Serializable

@Serializable
data class CommentsResponseDto(
    val data: CommentsDataDto
)