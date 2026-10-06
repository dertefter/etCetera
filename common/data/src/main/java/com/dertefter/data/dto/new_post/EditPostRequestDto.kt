package com.dertefter.data.dto.new_post

import com.dertefter.data.dto.feed.SpanDto
import kotlinx.serialization.Serializable

@Serializable
data class EditPostRequestDto(
    val content: String,
    val spans: List<SpanDto> = emptyList()
)