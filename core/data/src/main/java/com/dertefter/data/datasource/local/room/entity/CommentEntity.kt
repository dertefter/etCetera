package com.dertefter.data.datasource.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dertefter.data.dto.comments.CommentDto
import com.dertefter.data.dto.comments.ReplyToDto
import com.dertefter.data.dto.feed.AttachmentDto
import com.dertefter.data.dto.feed.AuthorDto

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey val id: String,
    val parentId: String?,
    val content: String,
    val author: AuthorDto,
    val likesCount: Int,
    val repliesCount: Int?,
    val isLiked: Boolean,
    val createdAt: String,
    val attachments: List<AttachmentDto>,
    val replyTo: ReplyToDto?
)

fun CommentDto.asEntity(parentId: String? = null): CommentEntity {
    return CommentEntity(
        id = id,
        parentId = parentId,
        content = content,
        author = author,
        likesCount = likesCount,
        repliesCount = repliesCount,
        isLiked = isLiked,
        createdAt = createdAt,
        attachments = attachments,
        replyTo = replyTo
    )
}

fun CommentEntity.asExternalModel(replies: List<CommentDto>? = null): CommentDto {
    return CommentDto(
        id = id,
        content = content,
        author = author,
        likesCount = likesCount,
        repliesCount = repliesCount,
        isLiked = isLiked,
        createdAt = createdAt,
        attachments = attachments,
        replies = replies,
        replyTo = replyTo
    )
}
