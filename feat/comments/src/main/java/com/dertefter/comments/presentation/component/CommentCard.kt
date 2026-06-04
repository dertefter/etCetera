package com.dertefter.comments.presentation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dertefter.comments.R
import com.dertefter.data.dto.comments.CommentDto
import com.dertefter.design.components.avatar.SmallEmojiAvatar
import com.dertefter.design.components.post.AttachmentsCarousel
import com.dertefter.design.components.post.LikeButton
import com.dertefter.comments.presentation.mapper.toUiModel
import com.dertefter.data.dto.feed.AttachmentDto
import com.dertefter.data.dto.feed.AuthorDto
import com.dertefter.design.components.avatar.DisplayName
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing

@Composable
fun CommentCard(
    comment: CommentDto,
    modifier: Modifier = Modifier,
    onLike: (commentId: String) -> Unit = {},
    onUnlike: (commentId: String) -> Unit = {},
    onLoadMoreReplies: (commentId: String) -> Unit = {},
    onUserClick: (userId: String) -> Unit = {},
) {
    var isExpanded by remember { mutableStateOf(false) }
    Box(
        modifier = modifier
            .padding(bottom = MaterialTheme.spacing.large)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable(onClick = { onUserClick(comment.author.id) })
                    .padding(horizontal = MaterialTheme.spacing.defaultScreenPadding),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large)
            ) {
                SmallEmojiAvatar(
                    emoji = comment.author.avatar,
                    containerSize = 40.dp
                )
                Column {
                    DisplayName(
                        name = comment.author.displayName,
                        verified = comment.author.verified,
                        hasNuksta = comment.author.hasNuksta,
                        pin = comment.author.pin?.toUiModel()
                    )
                    Text(
                        text = "@${comment.author.username}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (comment.content.isNotEmpty()) {
                Text(
                    text = comment.content,
                    modifier = Modifier.padding(horizontal = MaterialTheme.spacing.defaultScreenPadding),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (comment.attachments.isNotEmpty()) {
                AttachmentsCarousel(
                    attachments = comment.attachments.map { it.toUiModel() },
                    itemShape = MaterialTheme.shapes.medium,
                    itemHeight = 160.dp
                )
            }

            Row(
                modifier = Modifier
                    .padding(horizontal = MaterialTheme.spacing.defaultScreenPadding)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
            ) {
                LikeButton(
                    likes = comment.likesCount,
                    isLiked = comment.isLiked,
                    onClick = { if (comment.isLiked) onUnlike(comment.id) else onLike(comment.id) }
                )
                comment.repliesCount?.let { repliesCount ->
                    if (repliesCount > 0) {
                    Text(
                        style = MaterialTheme.typography.labelMediumEmphasized,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .clickable { isExpanded = !isExpanded },
                        text = pluralStringResource(
                            R.plurals.comments_reply_count,
                            repliesCount,
                            repliesCount
                        )
                    )
                }
                }

            }

            AnimatedVisibility(visible = isExpanded && (comment.repliesCount ?: 0) > 0) {
                val outlineVariant = MaterialTheme.colorScheme.outlineVariant
                val spacing = MaterialTheme.spacing
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = spacing.large)
                        .drawBehind {
                            val strokeWidth = 2.dp.toPx()
                            val lineX = spacing.medium.toPx() + strokeWidth / 2
                            drawLine(
                                color = outlineVariant,
                                start = Offset(lineX, 0f),
                                end = Offset(lineX, size.height),
                                strokeWidth = strokeWidth
                            )
                        }
                        .padding(start = spacing.medium * 2 + 2.dp),
                    verticalArrangement = Arrangement.spacedBy(spacing.medium)
                ) {
                    comment.replies?.forEach { reply ->
                        CommentCard(
                            comment = reply,
                            onLike = { onLike(reply.id) },
                            onUnlike = { onUnlike(reply.id) },
                            onLoadMoreReplies = onLoadMoreReplies,
                            onUserClick = { onUserClick(it) }
                        )
                    }
                    if ((comment.repliesCount ?: 0) > (comment.replies?.size ?: 0)) {
                        Text(
                            text = stringResource(R.string.comments_load_more),
                            style = MaterialTheme.typography.labelMediumEmphasized,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .clickable { onLoadMoreReplies(comment.id) }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CommentCardPreview() {
    AppTheme {
        CommentCard(
            comment = CommentDto(
                id = "1",
                content = "This is a sample comment content. It can be long enough to span multiple lines and test the layout of the CommentCard.",
                author = AuthorDto(
                    id = "author1",
                    avatar = "😊",
                    username = "johndoe",
                    verified = true,
                    hasNuksta = false,
                    displayName = "John Doe"
                ),
                likesCount = 42,
                repliesCount = 3,
                isLiked = false,
                createdAt = "2023-10-27T12:00:00Z",
                attachments = listOf(
                    AttachmentDto(
                        id = "1",
                        type = "image",
                        url = "https://picsum.photos/400/300",
                        width = 400,
                        height = 300,
                        mimeType = "image/jpeg",
                        filename = "image1.jpg",
                        size = 1000
                    )
                ),
                replies = listOf(
                    CommentDto(
                        id = "2",
                        content = "This is a reply to the first comment.",
                        author = AuthorDto(
                            id = "author2",
                            avatar = "😎",
                            username = "janedoe",
                            verified = false,
                            hasNuksta = false,
                            displayName = "Jane Doe"
                        ),
                        likesCount = 5,
                        repliesCount = 0,
                        isLiked = true,
                        createdAt = "2023-10-27T13:00:00Z"
                    )
                )
            )
        )
    }
}
