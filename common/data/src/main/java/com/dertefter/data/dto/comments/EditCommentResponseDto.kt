package com.dertefter.data.dto.comments

import kotlinx.serialization.Serializable

@Serializable
data class EditCommentResponseDto(
    val id: String,
    val content: String,
    val editedAt: String
)
