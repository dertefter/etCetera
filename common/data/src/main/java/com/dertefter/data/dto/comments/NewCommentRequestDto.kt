package com.dertefter.data.dto.comments

import com.dertefter.data.dto.feed.SpanDto
import kotlinx.serialization.Serializable

@Serializable
data class NewCommentRequestDto(
    val content: String?,
    val spans: List<SpanDto>? = null,
    val replyToUserId: String? = null,
    val attachmentIds: List<String>? = null,
)