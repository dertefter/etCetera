package com.dertefter.data.dto.new_post

import com.dertefter.data.dto.feed.SpanDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EditPostRequestDto(
    @SerialName("content") val content: String,
    @SerialName("spans") val spans: List<SpanDto> = emptyList()
)