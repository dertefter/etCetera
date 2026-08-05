package com.dertefter.user.presentation.mapper

import com.dertefter.data.dto.feed.AttachmentDto
import com.dertefter.data.dto.feed.AuthorDto
import com.dertefter.data.dto.feed.OriginalPostDto
import com.dertefter.data.dto.feed.PinDto
import com.dertefter.data.dto.feed.PollDto
import com.dertefter.data.dto.feed.PollOptionDto
import com.dertefter.data.dto.feed.PostDto
import com.dertefter.data.dto.feed.ShortAuthorDto
import com.dertefter.data.dto.feed.SpanDto
import com.dertefter.data.dto.user.UserDto
import com.dertefter.design.components.poll.PollOptionUiModel
import com.dertefter.design.components.poll.PollUiModel
import com.dertefter.design.components.post.AttachmentUiModel
import com.dertefter.design.components.post.AuthorUiModel
import com.dertefter.design.components.post.OriginalPostUiModel
import com.dertefter.design.components.post.PinUiModel
import com.dertefter.design.components.post.PostUiModel
import com.dertefter.design.components.post.SpanUiModel
import com.dertefter.navigation.AttachmentNavigationModel

fun PostDto.toUiModel(): PostUiModel {
    return PostUiModel(
        id = id,
        content = content,
        spans = spans.map { it.toUiModel() },
        author = author.toUiModel(),
        attachments = attachments.map { it.toUiModel() },
        poll = poll?.toUiModel(),
        likesCount = likesCount,
        isLiked = isLiked,
        commentsCount = commentsCount,
        repostsCount = repostsCount,
        isReposted = isReposted,
        viewsCount = viewsCount,
        dominantEmoji = dominantEmoji,
        isPinned = isPinned,
        isOwner = isOwner,
        createdAt = createdAt,
        editedAt = editedAt,
        originalPost = originalPost?.toUiModel()
    )
}

fun PollDto.toUiModel() = PollUiModel(
    id = id,
    title = question,
    options = options.map { it.toUiModel(votedOptionIds.contains(it.id)) },
    totalCount = totalVotes,
    isMultipleChoice = multipleChoice
)

fun PollOptionDto.toUiModel(isChecked: Boolean) = PollOptionUiModel(
    text = text,
    id = id,
    votesCount = votesCount,
    isChecked = isChecked
)

fun AttachmentUiModel.toNavigationModel() = AttachmentNavigationModel(id, type, url, mimeType)

fun UserDto.toUiModel() = AuthorUiModel(id, username, displayName, avatar, hasNuksta, verified, pin?.toUiModel())
fun AuthorDto.toUiModel() = AuthorUiModel(id, username, displayName, avatar, hasNuksta, verified, pin?.toUiModel())
fun ShortAuthorDto.toUiModel() = AuthorUiModel(id, username, displayName, avatar, hasNuksta, verified, pin?.toUiModel())
fun PinDto.toUiModel() = PinUiModel(description, name, slug, url)
fun AttachmentDto.toUiModel() = AttachmentUiModel(id, type, url, mimeType)

fun SpanDto.toUiModel() = SpanUiModel(type, length, offset, username, tag, url)

fun OriginalPostDto.toUiModel() = OriginalPostUiModel(
    id = id,
    content = content,
    spans = spans.map { it.toUiModel() },
    author = author.toUiModel(),
    attachments = attachments.map { it.toUiModel() },
    poll = poll?.toUiModel(),
    createdAt = createdAt,
    editedAt = null,
    isDeleted = isDeleted
)
