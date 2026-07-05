package com.dertefter.design.components.comment

import com.dertefter.design.components.post.AttachmentUiModel
import com.dertefter.design.components.post.AuthorUiModel

data class CommentUiModel(
    val id: String,
    val content: String,
    val author: AuthorUiModel,
    val likesCount: Int,
    val repliesCount: Int?,
    val isLiked: Boolean,
    val createdAt: String,
    val replyTo: ReplyToUiModel? = null,
    val attachments: List<AttachmentUiModel> = emptyList(),
    val replies: List<CommentUiModel>? = emptyList(),
    val isOwner: Boolean = false
)

data class ReplyToUiModel(
    val id: String,
    val username: String,
    val displayName: String
)
