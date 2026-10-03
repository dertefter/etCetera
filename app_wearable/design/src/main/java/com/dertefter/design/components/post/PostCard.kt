package com.dertefter.design.components.post

import android.content.ClipData
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.Dialog
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.IconButton
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import com.dertefter.design.R
import com.dertefter.design.components.avatar.Avatar
import com.dertefter.design.components.avatar.DisplayName
import com.dertefter.design.components.poll.PollCard
import com.dertefter.design.components.poll.PollUiModel
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.WearableTheme
import com.dertefter.design.theme.spacing
import kotlinx.coroutines.launch

@Composable
fun PostCard(
    post: PostUiModel,
    modifier: Modifier = Modifier,
    onLike: () -> Unit,
    onUnlike: () -> Unit,
    onCommentsClick: () -> Unit,
    onRepostClick: () -> Unit,
    onUserClick: (userId: String) -> Unit,
    onVote: (optionIds: List<String>) -> Unit,
    onEdit: () -> Unit = {},
    onPin: () -> Unit,
    onUnpin: () -> Unit,
    onDelete: () -> Unit,
    showCommentsButton: Boolean = true,
    isOnMyWall: Boolean = false,
    onOpenPost: (String) -> Unit,
    onHashtagClick: (hashtagId: String) -> Unit,
    onLinkClick: ((url: String) -> Unit)? = null,
    onAttachmentClick: (attachments: List<AttachmentUiModel>, position: Int) -> Unit
) {
    val uriHandler = LocalUriHandler.current
    val finalOnLinkClick = onLinkClick ?: { url -> uriHandler.openUri(url) }

    Box(
        modifier = modifier
            .clickable(onClick = { onOpenPost(post.id) })
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
        ) {
            PostHeader(
                post = post,
                isOnMyWall = isOnMyWall,
                onUserClick = onUserClick,
                onEdit = onEdit,
                onPin = onPin,
                onUnpin = onUnpin,
                onDelete = onDelete
            )
            PostContent(
                post = post,
                onOpenPost = onOpenPost,
                onHashtagClick = onHashtagClick,
                onUserClick = onUserClick,
                onLinkClick = finalOnLinkClick
            )
            PostAttachments(
                attachments = post.attachments,
                onAttachmentClick = onAttachmentClick
            )
            PostPoll(
                poll = post.poll,
                onVote = onVote
            )
            PostOriginalPost(
                originalPost = post.originalPost,
                onOpenPost = onOpenPost,
                onHashtagClick = onHashtagClick,
                onUserClick = onUserClick,
                onLinkClick = finalOnLinkClick,
                onAttachmentClick = onAttachmentClick
            )
            PostFooter(
                post = post,
                showCommentsButton = showCommentsButton,
                onLike = onLike,
                onUnlike = onUnlike,
                onCommentsClick = onCommentsClick,
                onRepostClick = onRepostClick
            )
        }
    }
}

@Composable
fun PostHeader(
    post: PostUiModel,
    modifier: Modifier = Modifier,
    isOnMyWall: Boolean = false,
    onUserClick: (userId: String) -> Unit,
    onEdit: () -> Unit = {},
    onPin: () -> Unit,
    onUnpin: () -> Unit,
    onDelete: () -> Unit
) {
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = { onUserClick(post.author.id) })
        ) {
            Avatar(
                emoji = post.author.avatar,
                modifier = Modifier.size(32.dp)
            )
            Column(
                modifier = Modifier.weight(1f)
            ) {
                DisplayName(
                    name = post.author.displayName,
                    verified = post.author.verified,
                    hasNuksta = post.author.hasNuksta,
                    pin = post.author.pin
                )
                Text(
                    text = "@${post.author.username}",
                    style = MaterialTheme.typography.bodyExtraSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        var showMenu by remember { mutableStateOf(false) }
        IconButton(
            modifier = Modifier.size(20.dp),
            onClick = { showMenu = true }
        ) {
            Icon(imageVector = Icons.MoreHoriz, contentDescription = "")
        }
        Dialog(
            visible = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            ScalingLazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = rememberScalingLazyListState(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
            ) {
                item {
                    Button(
                        onClick = {
                            val link = "https://xn--d1ah4a.com/@${post.author.username}/post/${post.id}"
                            scope.launch {
                                clipboard.setClipEntry(
                                    ClipEntry(ClipData.newPlainText(null, link))
                                )
                            }
                            showMenu = false
                        },
                        icon = { Icon(Icons.ContentCopy, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.design_post_copy_link))
                    }
                }
                if (isOnMyWall) {
                    item {
                        Button(
                            onClick = {
                                if (post.isPinned) onUnpin() else onPin()
                                showMenu = false
                            },
                            icon = {
                                Icon(
                                    if (post.isPinned) Icons.KeepOff else Icons.Keep,
                                    contentDescription = null
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                if (post.isPinned) stringResource(R.string.design_post_unpin)
                                else stringResource(R.string.design_post_pin)
                            )
                        }
                    }
                }
                if (post.isOwner) {
                    item {
                        Button(
                            onClick = {
                                onEdit()
                                showMenu = false
                            },
                            icon = { Icon(Icons.Edit, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(R.string.design_post_edit))
                        }
                    }
                }
                if (post.isOwner || isOnMyWall) {
                    item {
                        Button(
                            onClick = {
                                onDelete()
                                showMenu = false
                            },
                            icon = {
                                Icon(
                                    imageVector = Icons.Delete,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                contentColor = MaterialTheme.colorScheme.error,
                                iconColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text(stringResource(R.string.design_post_delete))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PostContent(
    post: PostUiModel,
    modifier: Modifier = Modifier,
    onOpenPost: (String) -> Unit,
    onHashtagClick: (hashtagId: String) -> Unit,
    onUserClick: (userId: String) -> Unit,
    onLinkClick: ((url: String) -> Unit)? = null
) {
    if (post.content.isNotEmpty()) {
        val uriHandler = LocalUriHandler.current
        val finalOnLinkClick = onLinkClick ?: { url -> uriHandler.openUri(url) }
        var revealedSpoilers by remember { mutableStateOf(setOf<Int>()) }
        val annotatedString = buildPostAnnotatedString(post.content, post.spans, revealedSpoilers)
        var layoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
        Text(
            modifier = modifier
                .fillMaxWidth()
                .pointerInput(post.id, revealedSpoilers) {
                    detectTapGestures { offset ->
                        layoutResult?.let { lr ->
                            val tapOffset = lr.getOffsetForPosition(offset)
                            val hashtagAnnotations = annotatedString.getStringAnnotations(
                                tag = "HASHTAG",
                                start = tapOffset,
                                end = tapOffset
                            )
                            val mentionAnnotations = annotatedString.getStringAnnotations(
                                tag = "MENTION",
                                start = tapOffset,
                                end = tapOffset
                            )
                            val spoilerAnnotations = annotatedString.getStringAnnotations(
                                tag = "SPOILER",
                                start = tapOffset,
                                end = tapOffset
                            )
                            val linkAnnotations = annotatedString.getStringAnnotations(
                                tag = "LINK",
                                start = tapOffset,
                                end = tapOffset
                            )
                            if (hashtagAnnotations.isNotEmpty()) {
                                onHashtagClick(hashtagAnnotations.first().item)
                            } else if (mentionAnnotations.isNotEmpty()) {
                                onUserClick(mentionAnnotations.first().item)
                            } else if (linkAnnotations.isNotEmpty()) {
                                finalOnLinkClick(linkAnnotations.first().item)
                            } else if (spoilerAnnotations.isNotEmpty()) {
                                spoilerAnnotations.firstOrNull()?.let { annotation ->
                                    revealedSpoilers = revealedSpoilers + annotation.item.toInt()
                                }
                            } else {
                                onOpenPost(post.id)
                            }
                        }
                    }
                },
            text = annotatedString,
            style = MaterialTheme.typography.bodyMedium,
            onTextLayout = { layoutResult = it }
        )
    }
}

@Composable
fun PostAttachments(
    attachments: List<AttachmentUiModel>,
    modifier: Modifier = Modifier,
    onAttachmentClick: (attachments: List<AttachmentUiModel>, position: Int) -> Unit
) {
    if (attachments.isNotEmpty()) {
        AttachmentsCarousel(
            attachments = attachments,
            itemShape = RoundedCornerShape(14.dp),
            itemHeight = 100.dp,
            onItemClick = { position ->
                onAttachmentClick(attachments, position)
            },
            modifier = modifier
        )
    }
}

@Composable
fun PostPoll(
    poll: PollUiModel?,
    modifier: Modifier = Modifier,
    onVote: (optionIds: List<String>) -> Unit
) {
    poll?.let { p ->
        PollCard(
            modifier = modifier,
            title = p.title,
            options = p.options,
            isMultipleChoice = p.isMultipleChoice,
            totalCount = p.totalCount,
            onVote = { optionIds -> onVote(optionIds) }
        )
    }
}

@Composable
fun PostOriginalPost(
    originalPost: OriginalPostUiModel?,
    modifier: Modifier = Modifier,
    onOpenPost: (String) -> Unit,
    onHashtagClick: (hashtagId: String) -> Unit,
    onUserClick: (userId: String) -> Unit,
    onLinkClick: ((url: String) -> Unit)? = null,
    onAttachmentClick: (attachments: List<AttachmentUiModel>, position: Int) -> Unit
) {
    originalPost?.let { origPost ->
        val uriHandler = LocalUriHandler.current
        val finalOnLinkClick = onLinkClick ?: { url -> uriHandler.openUri(url) }
        OriginalPostCard(
            modifier = modifier,
            originalPost = origPost,
            onOpenPost = { origId -> onOpenPost(origId) },
            onHashtagClick = onHashtagClick,
            onUserClick = onUserClick,
            onLinkClick = finalOnLinkClick,
            onAttachmentClick = { attachments, position ->
                onAttachmentClick(attachments, position)
            }
        )
    }
}

@Composable
fun PostFooter(
    post: PostUiModel,
    modifier: Modifier = Modifier,
    showCommentsButton: Boolean = true,
    onLike: () -> Unit,
    onUnlike: () -> Unit,
    onCommentsClick: () -> Unit,
    onRepostClick: () -> Unit
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
    ) {
        LikeButton(
            likes = post.likesCount,
            isLiked = post.isLiked,
            onClick = if (post.isLiked) onUnlike else onLike
        )
        if (showCommentsButton) {
            CommentsButton(
                comments = post.commentsCount,
                onClick = onCommentsClick,
            )
        }
        RepostButton(
            reposts = post.repostsCount,
            isReposted = post.isReposted,
            onClick = onRepostClick,
        )
    }
}

@Preview(device = "id:wearos_small_round")
@Composable
fun PostCardPreview() {
    WearableTheme {
        AppScaffold {
            PostCard(
                modifier = Modifier.padding(horizontal = MaterialTheme.spacing.defaultScreenPadding),
                post = PostUiModel(
                    id = "1",
                    content = "#супермиликотики",
                    spans = emptyList(),
                    author = AuthorUiModel(
                        id = "author1",
                        username = "johndffffffffffoe",
                        displayName = "Johnffffffffffffffffffff Doe",
                        avatar = "😐",
                        hasNuksta = true,
                        verified = true,
                        pin = null
                    ),
                    attachments = listOf(
                        AttachmentUiModel(
                            id = "1", type = "image", url = "https://picsum.photos/400/300"
                        )
                    ),
                    poll = null,
                    likesCount = 100,
                    isLiked = true,
                    commentsCount = 50,
                    repostsCount = 2,
                    isReposted = true,
                    viewsCount = 100,
                    dominantEmoji = "🦎",
                    isPinned = true,
                    isOwner = false,
                    createdAt = "2024-08-05T12:00:00Z",
                    editedAt = null,
                    originalPost = null,
                ),
                isOnMyWall = true,
                onHashtagClick = {},
                onAttachmentClick = { _, _ -> },
                onOpenPost = {},
                onDelete = {},
                onCommentsClick = {},
                onPin = {},
                onUnpin = {},
                onVote = {},
                onLike = {},
                onUnlike = {},
                onUserClick = {},
                onRepostClick = {},
            )
        }
    }
}
