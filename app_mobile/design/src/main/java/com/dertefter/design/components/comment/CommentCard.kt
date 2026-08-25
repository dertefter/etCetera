package com.dertefter.design.components.comment

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dertefter.design.R
import com.dertefter.design.components.avatar.DisplayName
import com.dertefter.design.components.avatar.EmojiAvatar
import com.dertefter.design.components.lists.SegmentedContentItem
import com.dertefter.design.components.post.AttachmentUiModel
import com.dertefter.design.components.post.AttachmentsCarousel
import com.dertefter.design.components.post.AuthorUiModel
import com.dertefter.design.components.post.LikeButton
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
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
    onReplyClick: (commentId: String, userId: String) -> Unit = { _, _ -> },
    meUserId: String? = null,
    index: Int,
    count: Int,
    isReply: Boolean = false
) {
    var isExpanded by remember { mutableStateOf(false) }

    val bgColor = if (!isReply)
        MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
    else
        MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.7f)

    Column{

        SegmentedContentItem(
            index = index,
            count = count,
            modifier = modifier
                .fillMaxWidth(),
            colors = ListItemDefaults.segmentedColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues()
        )
        {
            Column(
                modifier = Modifier
                    .background(bgColor)
                    .padding(MaterialTheme.spacing.large)
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
                        modifier = Modifier
                            .clickable(onClick = { onUserClick(comment.author.id) })
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
                    )
                    {
                        EmojiAvatar(
                            emoji = comment.author.avatar,
                            containerSize = 40.dp
                        )
                        Column {
                            DisplayName(
                                name = comment.author.displayName,
                                verified = comment.author.verified,
                                hasNuksta = comment.author.hasNuksta,
                                pin = comment.author.pin
                            )
                            Text(
                                text = "@${comment.author.username}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    var showMenu by remember { mutableStateOf(false) }

                    val isOwner = meUserId == comment.author.id

                    Box {
                        IconButton(
                            onClick = { showMenu = true }) {
                            Icon(
                                imageVector = Icons.MoreHoriz,
                                contentDescription = stringResource(R.string.design_comment_actions)
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            shape = MaterialTheme.shapes.largeIncreased,
                            onDismissRequest = { showMenu = false }) {

                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.design_comment_report)) },
                                onClick = {
                                    onEdit(comment.id)
                                    showMenu = false
                                },
                                leadingIcon = {
                                    Icon(Icons.Error,
                                        contentDescription = stringResource(R.string.design_comment_report)
                                    )
                                })

                            if (isOwner) {
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.design_post_edit)) },
                                    onClick = {
                                        onEdit(comment.id)
                                        showMenu = false
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Edit,
                                            contentDescription = stringResource(R.string.design_post_edit)
                                        )
                                    })


                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.design_post_delete)) },
                                    onClick = {
                                        onDelete(comment.id)
                                        showMenu = false
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Delete,
                                            contentDescription = stringResource(R.string.design_post_delete),
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    })
                            }
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
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                if (comment.attachments.isNotEmpty()) {
                    AttachmentsCarousel(
                        attachments = comment.attachments,
                        itemShape = MaterialTheme.shapes.medium,
                        itemHeight = 180.dp
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large),
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
                                style = MaterialTheme.typography.labelMediumEmphasized,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .weight(1f)
                                    .align(Alignment.CenterVertically)
                                    .clickable { isExpanded = !isExpanded },
                                text = pluralStringResource(
                                    R.plurals.design_comment_reply_count,
                                    repliesCount,
                                    repliesCount
                                )
                            )
                        }
                    }
                    Text(
                        color = MaterialTheme.colorScheme.primary,
                        text = stringResource(R.string.design_comment_reply),
                        style = MaterialTheme.typography.bodyMediumEmphasized,
                        modifier = Modifier
                            .clickable(
                                onClick = {
                                    onReplyClick(
                                        comment.id, comment.author.id
                                    )
                                }

                            )
                    )

                }
            }
        }

        AnimatedVisibility(visible = isExpanded && (comment.repliesCount ?: 0) > 0) {
            val spacing = MaterialTheme.spacing
            Column(
                modifier = Modifier
                    .padding(top = MaterialTheme.spacing.small)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(spacing.small)
            ) {
                comment.replies?.forEachIndexed { index, reply ->
                    CommentCard(
                        comment = reply,
                        onLike = { onLike(reply.id) },
                        onUnlike = { onUnlike(reply.id) },
                        onLoadMoreReplies = onLoadMoreReplies,
                        onUserClick = { onUserClick(it) },
                        onReplyClick = { _, userId ->
                            onReplyClick(
                                comment.id,
                                userId
                            )
                        },
                        onDelete = {onDelete(it)},
                        meUserId = meUserId,
                        index = index,
                        count = comment.replies.count(),
                        isReply = true
                    )
                }
                if ((comment.repliesCount ?: 0) > (comment.replies?.size ?: 0)) {
                    Text(
                        text = stringResource(R.string.design_comment_load_more),
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

@Preview(showBackground = false)
@Composable
fun CommentCardPreview() {
    AppTheme {
        CommentCard(
            index = 0,
            count = 0,
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
