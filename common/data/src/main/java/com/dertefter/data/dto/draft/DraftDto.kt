package com.dertefter.data.dto.draft

import com.dertefter.data.dto.feed.SpanDto
import com.dertefter.data.dto.new_post.NewPollDto
import kotlinx.serialization.Serializable

@Serializable
data class DraftDto(
    val content: String = "",
    val spans: List<SpanDto> = emptyList(),
    val poll: NewPollDto? = null,
    val attachmentIds: List<String> = emptyList(),
    val wallRecipientId: String? = null,
    val postId: String? = null,
    val commentId: String? = null,
    val replyToUserId: String? = null
)
