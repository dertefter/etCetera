package com.dertefter.user.presentation.mapper

import com.dertefter.data.dto.feed.AttachmentDto
import com.dertefter.data.dto.feed.AuthorDto
import com.dertefter.data.dto.feed.OriginalPostDto
import com.dertefter.data.dto.feed.PollDto
import com.dertefter.data.dto.feed.PollOptionDto
import com.dertefter.data.dto.feed.PostDto
import com.dertefter.data.dto.feed.ShortAuthorDto
import com.dertefter.design.components.poll.PollOptionUiModel
import com.dertefter.design.components.poll.PollUiModel
import com.dertefter.design.components.post.AttachmentUiModel
import com.dertefter.design.components.post.AuthorUiModel
import com.dertefter.design.components.post.OriginalPostUiModel
import com.dertefter.design.components.post.PostUiModel

fun PostDto.toUiModel(isPinned: Boolean? = null): PostUiModel {
    return PostUiModel(
        id = id,
        content = content,
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
        originalPost = originalPost?.toUiModel(),
        isPinned = isPinned,
        isOwner = isOwner
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

fun AuthorDto.toUiModel() = AuthorUiModel(id, username, displayName, avatar)
fun ShortAuthorDto.toUiModel() = AuthorUiModel(id, username, displayName, avatar)
fun AttachmentDto.toUiModel() = AttachmentUiModel(id, type, url, mimeType)
fun OriginalPostDto.toUiModel() = OriginalPostUiModel(
    id = id,
    content = content,
    author = author.toUiModel(),
    attachments = attachments.map { it.toUiModel() },
    poll = poll?.toUiModel(),
    isDeleted = isDeleted
)
