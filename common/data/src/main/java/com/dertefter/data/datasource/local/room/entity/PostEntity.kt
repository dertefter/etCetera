package com.dertefter.data.datasource.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dertefter.data.dto.feed.AttachmentDto
import com.dertefter.data.dto.feed.AuthorDto
import com.dertefter.data.dto.feed.OriginalPostDto
import com.dertefter.data.dto.feed.PollDto
import com.dertefter.data.dto.feed.PostDto
import com.dertefter.data.dto.feed.SpanDto

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey val id: String,
    val content: String,
    val spans: List<SpanDto>,
    val likesCount: Int,
    val commentsCount: Int,
    val repostsCount: Int,
    val viewsCount: Int,
    val authorId: String?,
    val wallRecipientId: String?,
    val createdAt: String,
    val author: AuthorDto,
    val attachments: List<AttachmentDto>,
    val isLiked: Boolean,
    val isReposted: Boolean,
    val isOwner: Boolean,
    val isViewed: Boolean,
    val originalPost: OriginalPostDto?,
    val poll: PollDto?,
    val dominantEmoji: String?,
    val editedAt: String?,
    val vs: String?
)

fun PostDto.asEntity(): PostEntity {
    return PostEntity(
        id = id,
        content = content,
        spans = spans,
        likesCount = likesCount,
        commentsCount = commentsCount,
        repostsCount = repostsCount,
        viewsCount = viewsCount,
        authorId = authorId,
        wallRecipientId = wallRecipientId,
        createdAt = createdAt,
        author = author,
        attachments = attachments,
        isLiked = isLiked,
        isReposted = isReposted,
        isOwner = isOwner,
        isViewed = isViewed,
        originalPost = originalPost,
        poll = poll,
        dominantEmoji = dominantEmoji,
        editedAt = editedAt,
        vs = vs
    )
}

fun PostEntity.asExternalModel(): PostDto {
    return PostDto(
        id = id,
        content = content,
        spans = spans,
        likesCount = likesCount,
        commentsCount = commentsCount,
        repostsCount = repostsCount,
        viewsCount = viewsCount,
        authorId = authorId,
        wallRecipientId = wallRecipientId,
        createdAt = createdAt,
        author = author,
        attachments = attachments,
        isLiked = isLiked,
        isReposted = isReposted,
        isOwner = isOwner,
        isViewed = isViewed,
        originalPost = originalPost,
        poll = poll,
        dominantEmoji = dominantEmoji,
        editedAt = editedAt,
        vs = vs
    )
}
