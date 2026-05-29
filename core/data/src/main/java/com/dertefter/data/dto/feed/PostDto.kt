package com.dertefter.data.dto.feed

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostDto(
    @SerialName("id") val id: String,
    @SerialName("content") val content: String,
    @SerialName("spans") val spans: List<SpanDto>,
    @SerialName("likesCount") val likesCount: Int = 0,
    @SerialName("commentsCount") val commentsCount: Int = 0,
    @SerialName("repostsCount") val repostsCount: Int = 0,
    @SerialName("viewsCount") val viewsCount: Int = 0,
    @SerialName("authorId") val authorId: String? = null,
    @SerialName("wallRecipientId") val wallRecipientId: String? = null,
    @SerialName("createdAt") val createdAt: String,
    @SerialName("author") val author: AuthorDto,
    @SerialName("attachments") val attachments: List<AttachmentDto>,
    @SerialName("isLiked") val isLiked: Boolean,
    @SerialName("isReposted") val isReposted: Boolean,
    @SerialName("isOwner") val isOwner: Boolean,
    @SerialName("isViewed") val isViewed: Boolean = false,
    @SerialName("originalPost") val originalPost: OriginalPostDto? = null,
    @SerialName("poll") val poll: PollDto? = null,
    @SerialName("dominantEmoji") val dominantEmoji: String? = null,
    @SerialName("editedAt") val editedAt: String? = null,
    @SerialName("vs") val vs: String? = null
)