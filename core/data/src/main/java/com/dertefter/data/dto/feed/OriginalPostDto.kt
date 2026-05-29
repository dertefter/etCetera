package com.dertefter.data.dto.feed

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OriginalPostDto(
    @SerialName("id") val id: String,
    @SerialName("content") val content: String,
    @SerialName("spans") val spans: List<SpanDto>,
    @SerialName("author") val author: ShortAuthorDto,
    @SerialName("attachments") val attachments: List<AttachmentDto>,
    @SerialName("likesCount") val likesCount: Int,
    @SerialName("commentsCount") val commentsCount: Int,
    @SerialName("repostsCount") val repostsCount: Int,
    @SerialName("viewsCount") val viewsCount: Int,
    @SerialName("createdAt") val createdAt: String,
    @SerialName("dominantEmoji") val dominantEmoji: String? = null,
    @SerialName("poll") val poll: PollDto? = null,
    @SerialName("isDeleted") val isDeleted: Boolean,
    @SerialName("vs") val vs: String
)