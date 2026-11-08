package com.dertefter.data.dto.comments

import com.dertefter.data.dto.feed.SpanDto
import kotlinx.serialization.Serializable

@Serializable
data class EditCommentRequestDto(
    val content: String,
    val spans: List<SpanDto> = emptyList()
)
