package com.dertefter.data.dto.feed

import kotlinx.serialization.Serializable

@Serializable
data class PostDto(
    val id: String,
    val content: String,
    val spans: List<SpanDto>,
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val repostsCount: Int = 0,
    val viewsCount: Int = 0,
    val authorId: String? = null,
    val wallRecipientId: String? = null,
    val createdAt: String,
    val author: AuthorDto,
    val attachments: List<AttachmentDto>,
    val isLiked: Boolean,
    val isReposted: Boolean,
    val isOwner: Boolean,
    val isViewed: Boolean = false,
    val originalPost: OriginalPostDto? = null,
    val poll: PollDto? = null,
    val dominantEmoji: String? = null,
    val editedAt: String? = null,
    val vs: String? = null,
    val isPinned: Boolean = false
)