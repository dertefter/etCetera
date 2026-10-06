package com.dertefter.data.dto.feed

import kotlinx.serialization.Serializable

@Serializable
data class OriginalPostDto(
    val id: String,
    val content: String,
    val spans: List<SpanDto>,
    val author: ShortAuthorDto,
    val attachments: List<AttachmentDto>,
    val likesCount: Int,
    val commentsCount: Int,
    val repostsCount: Int,
    val viewsCount: Int,
    val isLiked: Boolean = false,
    val isReposted: Boolean = false,
    val createdAt: String,
    val dominantEmoji: String? = null,
    val poll: PollDto? = null,
    val isDeleted: Boolean,
    val vs: String? = null
)