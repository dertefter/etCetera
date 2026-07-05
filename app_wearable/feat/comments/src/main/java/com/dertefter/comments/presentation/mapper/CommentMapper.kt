package com.dertefter.comments.presentation.mapper

import com.dertefter.data.dto.comments.CommentDto
import com.dertefter.data.dto.comments.ReplyToDto
import com.dertefter.data.dto.feed.AttachmentDto
import com.dertefter.data.dto.feed.AuthorDto
import com.dertefter.data.dto.feed.PinDto
import com.dertefter.design.components.comment.CommentUiModel
import com.dertefter.design.components.comment.ReplyToUiModel
import com.dertefter.design.components.post.AttachmentUiModel
import com.dertefter.design.components.post.AuthorUiModel
import com.dertefter.design.components.post.PinUiModel

fun PinDto.toUiModel() = PinUiModel(description, name, slug, url)
fun AttachmentDto.toUiModel() = AttachmentUiModel(id, type, url, mimeType)
fun AuthorDto.toUiModel() = AuthorUiModel(id, username, displayName, avatar, hasNuksta, verified, pin?.toUiModel())
fun ReplyToDto.toUiModel() = ReplyToUiModel(id, username, displayName)

fun CommentDto.toUiModel(meUserId: String? = null): CommentUiModel {
    return CommentUiModel(
        id = id,
        content = content,
        author = author.toUiModel(),
        likesCount = likesCount,
        repliesCount = repliesCount,
        isLiked = isLiked,
        createdAt = createdAt,
        replyTo = replyTo?.toUiModel(),
        attachments = attachments.map { it.toUiModel() },
        replies = replies?.map { it.toUiModel(meUserId) },
        isOwner = author.id == meUserId
    )
}
