package com.dertefter.data.dto.new_post

import com.dertefter.data.dto.feed.SpanDto
import kotlinx.serialization.Serializable

@Serializable
data class NewPostRequestDto(
    val content: String,
    val spans: List<SpanDto> = emptyList(),
    val poll: NewPollDto? = null,
    val attachmentIds: List<String> = emptyList(),
    val wallRecipientId: String? = null
)