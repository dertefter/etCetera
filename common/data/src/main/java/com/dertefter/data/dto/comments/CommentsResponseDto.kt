package com.dertefter.data.dto.comments

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentsResponseDto(
    @SerialName("data") val data: CommentsDataDto
)