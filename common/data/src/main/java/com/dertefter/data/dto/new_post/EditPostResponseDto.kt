package com.dertefter.data.dto.new_post

import com.dertefter.data.dto.feed.AttachmentDto
import com.dertefter.data.dto.feed.AuthorDto
import com.dertefter.data.dto.feed.OriginalPostDto
import com.dertefter.data.dto.feed.PollDto
import com.dertefter.data.dto.feed.SpanDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EditPostResponseDto(
    @SerialName("id") val id: String,
    @SerialName("content") val content: String,
    @SerialName("spans") val spans: List<SpanDto>,
    @SerialName("updatedAt") val updatedAt: String,
    val isPinned: Boolean = false
)