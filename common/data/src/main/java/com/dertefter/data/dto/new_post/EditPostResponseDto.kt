package com.dertefter.data.dto.new_post

import com.dertefter.data.dto.feed.SpanDto
import kotlinx.serialization.Serializable

@Serializable
data class EditPostResponseDto(
    val id: String,
    val content: String,
    val spans: List<SpanDto>,
    val updatedAt: String,
    val isPinned: Boolean = false
)