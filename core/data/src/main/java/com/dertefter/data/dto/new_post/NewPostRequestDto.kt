package com.dertefter.data.dto.new_post

import com.dertefter.data.dto.feed.SpanDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NewPostRequestDto(
    @SerialName("content") val content: String,
    @SerialName("spans") val spans: List<SpanDto> = emptyList(),
    @SerialName("poll") val poll: NewPollDto? = null,
    @SerialName("attachmentIds") val attachmentIds: List<String> = emptyList(),
    @SerialName("wallRecipientId") val wallRecipientId: String? = null
)