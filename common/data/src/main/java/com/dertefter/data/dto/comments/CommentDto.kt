package com.dertefter.data.dto.comments

import com.dertefter.data.dto.feed.AttachmentDto
import com.dertefter.data.dto.feed.AuthorDto
import kotlinx.serialization.Serializable

@Serializable
data class CommentDto(
    val id: String,
    val content: String,
    val author: AuthorDto,
    val likesCount: Int,
    val repliesCount: Int? = 0,
    val isLiked: Boolean,
    val createdAt: String,
    val attachments: List<AttachmentDto> = emptyList(),
    val replies: List<CommentDto>? = emptyList(),
    val replyTo: ReplyToDto? = null
)