package com.dertefter.design.components.post

import com.dertefter.design.components.poll.PollUiModel

data class PostUiModel(
    val id: String,
    val content: String,
    val spans: List<SpanUiModel>,
    val author: AuthorUiModel,
    val attachments: List<AttachmentUiModel>,
    val poll: PollUiModel?,
    val likesCount: Int,
    val isLiked: Boolean,
    val commentsCount: Int,
    val repostsCount: Int,
    val isReposted: Boolean,
    val viewsCount: Int,
    val dominantEmoji: String?,
    val isPinned: Boolean,
    val isOwner: Boolean,
    val createdAt: String,
    val editedAt: String?,
    val originalPost: OriginalPostUiModel?
)

data class SpanUiModel(
    val type: String,
    val length: Int,
    val offset: Int,
    val username: String? = null,
    val tag: String? = null,
    val url: String? = null
)

data class AuthorUiModel(
    val id: String,
    val username: String,
    val displayName: String,
    val avatar: String,
    val hasNuksta: Boolean,
    val verified: Boolean,
    val pin: PinUiModel?
)

data class AttachmentUiModel(
    val id: String,
    val type: String, // "image", "video"
    val url: String?,
    val mimeType: String? = null
)

data class PinUiModel(
    val description: String,
    val name: String,
    val slug: String?,
    val url: String?
)

data class OriginalPostUiModel(
    val id: String,
    val content: String,
    val spans: List<SpanUiModel>,
    val author: AuthorUiModel,
    val attachments: List<AttachmentUiModel>,
    val poll: PollUiModel?,
    val createdAt: String,
    val editedAt: String?,
    val isDeleted: Boolean
)
