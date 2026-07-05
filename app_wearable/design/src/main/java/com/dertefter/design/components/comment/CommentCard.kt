package com.dertefter.design.components.comment

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import com.dertefter.design.R
import com.dertefter.design.components.avatar.DisplayName
import com.dertefter.design.components.avatar.SmallEmojiAvatar
import com.dertefter.design.components.post.AttachmentUiModel
import com.dertefter.design.components.post.AttachmentsCarousel
import com.dertefter.design.components.post.AuthorUiModel
import com.dertefter.design.components.post.LikeButton
import com.dertefter.design.theme.WearableTheme
import com.dertefter.design.theme.spacing

@Composable
fun CommentCard(
    comment: CommentUiModel,
    modifier: Modifier = Modifier,
    onLike: (commentId: String) -> Unit = {},
    onUnlike: (commentId: String) -> Unit = {},
    onLoadMoreReplies: (commentId: String) -> Unit = {},
    onUserClick: (userId: String) -> Unit = {},
    onEdit: (commentId: String) -> Unit = {},
    onDelete: (commentId: String) -> Unit = {},
    onReplyClick: ((commentId: String, userId: String) -> Unit)? = null,
    meUserId: String? = null,
) {
    var isExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
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
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
                    modifier = Modifier
                        .clickable(
                            onClick = { onUserClick(comment.author.id) })
                )
                {
                    SmallEmojiAvatar(
                        emoji = comment.author.avatar,
                        modifier = Modifier.size(32.dp),
                        fontSize = 14.sp
                    )
                    Column()
                    {
                        DisplayName(
                            name = comment.author.displayName,
                            verified = comment.author.verified,
                            hasNuksta = comment.author.hasNuksta,
                            pin = comment.author.pin
                        )
                        Text(
                            text = "@${comment.author.username}",
                            style = MaterialTheme.typography.bodyExtraSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            if (comment.content.isNotEmpty()) {
                val annotatedString = buildAnnotatedString {
                    comment.replyTo?.let { replyTo ->
                        withLink(
                            LinkAnnotation.Clickable(
                                tag = "user",
                                linkInteractionListener = {
                                    onUserClick(replyTo.id)
                                }
                            )
                        ) {
                            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                                append("@${replyTo.username}, ")
                            }
                        }
                    }
                    append(comment.content)
                }
                Text(
                    text = annotatedString,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (comment.attachments.isNotEmpty()) {
                AttachmentsCarousel(
                    attachments = comment.attachments,
                    itemShape = MaterialTheme.shapes.medium,
                    itemHeight = 60.dp
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LikeButton(
                    likes = comment.likesCount,
                    isLiked = comment.isLiked,
                    onClick = { if (comment.isLiked) onUnlike(comment.id) else onLike(comment.id) }
                )
                comment.repliesCount?.let { repliesCount ->
                    if (repliesCount > 0) {
                    Text(
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .weight(1f)
                            .align(Alignment.CenterVertically)
                            .clickable { isExpanded = !isExpanded },
                        textAlign = TextAlign.End,
                        text = pluralStringResource(
                            R.plurals.design_comment_reply_count,
                            repliesCount,
                            repliesCount
                        )
                    )
                }
                }
                onReplyClick?.let { onReplyClick ->
                    Text(
                        color = MaterialTheme.colorScheme.primary,
                        text = stringResource(R.string.design_comment_reply),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .clickable(
                                onClick = {
                                    onReplyClick(
                                        comment.id, comment.author.id
                                    )
                                }
                            )
                            .padding(horizontal = MaterialTheme.spacing.defaultScreenPadding)
                    )
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
                            onUserClick = { onUserClick(it) },
                            onReplyClick = onReplyClick,
                            onDelete = {onDelete(it)},
                            meUserId = meUserId,
                        )
                    }
                    if ((comment.repliesCount ?: 0) > (comment.replies?.size ?: 0)) {
                        Text(
                            text = stringResource(R.string.design_comment_load_more),
                            style = MaterialTheme.typography.labelMedium,
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

@Preview(device = "id:wearos_large_round")
@Composable
fun CommentCardPreview() {
    WearableTheme {
        CommentCard(
            comment = CommentUiModel(
                id = "1",
                content = "This is a sample comment content. It can be long enough to span multiple lines and test the layout of the CommentCard.",
                author = AuthorUiModel(
                    id = "author1",
                    avatar = "😊",
                    username = "johndoe",
                    verified = true,
                    hasNuksta = false,
                    displayName = "John Doe",
                    pin = null
                ),
                likesCount = 42,
                repliesCount = 3,
                isLiked = false,
                createdAt = "2023-10-27T12:00:00Z",
                replyTo = ReplyToUiModel(
                    id = "dddd",
                    username = "ddddddd",
                    displayName = "уииии"
                ),
                attachments = listOf(
                    AttachmentUiModel(
                        id = "1",
                        type = "image",
                        url = "https://picsum.photos/400/300",
                        mimeType = "image/jpeg"
                    )
                ),
                replies = listOf(
                    CommentUiModel(
                        id = "2",
                        content = "This is a reply to the first comment.",
                        author = AuthorUiModel(
                            id = "author2",
                            avatar = "😎",
                            username = "janedoe",
                            verified = false,
                            hasNuksta = false,
                            displayName = "Jane Doe",
                            pin = null
                        ),
                        likesCount = 5,
                        repliesCount = 0,
                        isLiked = true,
                        createdAt = "2023-10-27T13:00:00Z"
                    )
                )
            ),
        )
    }
}
