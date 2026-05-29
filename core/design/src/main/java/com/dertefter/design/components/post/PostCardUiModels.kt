package com.dertefter.design.components.post

import com.dertefter.design.components.poll.PollUiModel

data class PostUiModel(
    val id: String,
    val content: String,
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
    val isPinned: Boolean? = null,
    val isOwner: Boolean,
    val originalPost: OriginalPostUiModel?
)

data class AuthorUiModel(
    val id: String,
    val username: String,
    val displayName: String,
    val avatar: String
)

data class AttachmentUiModel(
    val id: String,
    val type: String, // "image", "video"
    val url: String?,
    val mimeType: String? = null
)

data class OriginalPostUiModel(
    val id: String,
    val content: String,
    val author: AuthorUiModel,
    val attachments: List<AttachmentUiModel>,
    val poll: PollUiModel?,
    val isDeleted: Boolean
)
