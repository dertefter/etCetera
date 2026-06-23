package com.dertefter.data.dto.comments

import com.dertefter.data.dto.feed.SpanDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NewCommentRequestDto(
    @SerialName("content") val content: String?,
    @SerialName("spans") val spans: List<SpanDto>? = null,
    @SerialName("replyToUserId") val replyToUserId: String? = null,
    @SerialName("attachmentIds") val attachmentIds: List<String>? = null,
)