package com.dertefter.design.components.post

import android.content.ClipData
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dertefter.design.R
import com.dertefter.design.components.avatar.DisplayName
import com.dertefter.design.components.avatar.SmallEmojiAvatar
import com.dertefter.design.components.poll.PollCard
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
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
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.extraLarge)
            .clickable(onClick = { onOpenPost(post.id) })
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(MaterialTheme.spacing.large)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large),
                    modifier = Modifier
                        .clickable(
                            onClick = { onUserClick(post.author.id) })
                ){
                    SmallEmojiAvatar(emoji = post.author.avatar)
                    Column()
                    {
                        DisplayName(
                            name = post.author.displayName,
                            verified = post.author.verified,
                            hasNuksta = post.author.hasNuksta,
                            pin = post.author.pin
                        )
                        Text(
                            text = "@${post.author.username}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }


                Spacer(
                    modifier = Modifier.weight(1f)
                )

                AnimatedVisibility(
                    visible = post.isPinned
                ) {
                    Icon(
                        imageVector = Icons.Keep,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                    )
                }

                var showMenu by remember { mutableStateOf(false) }
                Box {
                    IconButton(
                        onClick = { showMenu = true }) {
                        Icon(
                            imageVector = Icons.MoreHoriz, contentDescription = ""
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        shape = MaterialTheme.shapes.largeIncreased,
                        onDismissRequest = { showMenu = false }) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.design_post_copy_link)) },
                            onClick = {
                                val link =
                                    "https://итд.com/@${post.author.username}/post/${post.id}"
                                scope.launch {
                                    clipboard.setClipEntry(
                                        ClipEntry(
                                            ClipData.newPlainText(
                                                null, link
                                            )
                                        )
                                    )
                                }
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(Icons.ContentCopy, contentDescription = null)
                            })
                        if (isOnMyWall){
                            DropdownMenuItem(text = {
                                Text(
                                    if (post.isPinned) stringResource(R.string.design_post_unpin)
                                    else stringResource(R.string.design_post_pin)
                                )
                            }, onClick = {
                                if (post.isPinned) onUnpin() else onPin()
                                showMenu = false
                            }, leadingIcon = {
                                Icon(
                                    if (post.isPinned) Icons.KeepOff else Icons.Keep,
                                    contentDescription = null
                                )
                            })
                        }
                        if (post.isOwner) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.design_post_edit)) },
                                onClick = {
                                    showMenu = false
                                },
                                leadingIcon = {
                                    Icon(Icons.Edit, contentDescription = null)
                                })
                        }

                        if (post.isOwner || isOnMyWall) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.design_post_delete)) },
                                onClick = {
                                    onDelete()
                                    showMenu = false
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Delete,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                })
                        }
                    }
                }
            }
            if (post.content.isNotEmpty()) {
                var revealedSpoilers by remember { mutableStateOf(setOf<Int>()) }
                val annotatedString = buildPostAnnotatedString(post.content, post.spans, revealedSpoilers)
                var layoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
                Text(
                    modifier = Modifier
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
                    onTextLayout = { layoutResult = it }
                )
            }
            if (post.attachments.isNotEmpty()) {
                AttachmentsCarousel(
                    attachments = post.attachments,
                    onItemClick = { position ->
                        onAttachmentClick(
                            post.attachments, position
                        )
                    })
            }
            post.poll?.let { poll ->
                PollCard(
                    title = poll.title,
                    options = poll.options,
                    isMultipleChoice = poll.isMultipleChoice,
                    totalCount = poll.totalCount,
                    onVote = { optionIds ->
                        onVote(optionIds)
                    })
            }
            post.originalPost?.let { originalPost ->
                OriginalPostCard(
                    originalPost = originalPost,
                    onOpenPost = { origId -> onOpenPost(origId) },
                    onHashtagClick = onHashtagClick,
                    onUserClick = onUserClick,
                    onLinkClick = finalOnLinkClick,
                    onAttachmentClick = { attachments, position ->
                        onAttachmentClick(attachments, position)
                    })
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
                ) {

                    LikeButton(
                        likes = post.likesCount,
                        isLiked = post.isLiked,
                        onClick = if (post.isLiked) {
                            onUnlike
                        } else {
                            onLike
                        }
                    )
                    if (showCommentsButton){
                        CommentsButton(
                            comments = post.commentsCount, onClick = onCommentsClick
                        )
                    }
                    RepostButton(
                        reposts = post.repostsCount,
                        isReposted = post.isReposted,
                        onClick = onRepostClick
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    post.dominantEmoji?.let { dominantEmoji ->
                        DominantEmoji(dominantEmoji = dominantEmoji)
                    }

                    Views(
                        views = post.viewsCount
                    )
                }
            }
        }
    }
}

@Preview(showBackground = false)
@Composable
fun PostCardPreview() {
    AppTheme {
        PostCard(
            post = PostUiModel(
                id = "1",
                content = "#супермиликотики",
                spans = emptyList(),
                author = AuthorUiModel(
                    id = "author1", username = "johndoe", displayName = "John Doe", avatar = "😐", hasNuksta = true, verified = true, pin = null
                ),
                attachments = listOf(
                    AttachmentUiModel(
                        id = "1", type = "image", url = "https://picsum.photos/400/300"
                    )
                ),
                likesCount = 10,
                isLiked = false,
                commentsCount = 5,
                repostsCount = 2,
                isReposted = true,
                viewsCount = 100,
                dominantEmoji = "🦎",
                isPinned = true,
                isOwner = false,
                originalPost = null,
                poll = null,
            ), isOnMyWall = true, onHashtagClick = {},
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
            onRepostClick = {}
        )
    }
}
