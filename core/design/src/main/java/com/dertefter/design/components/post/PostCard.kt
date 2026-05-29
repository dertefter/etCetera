package com.dertefter.design.components.post

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import com.dertefter.design.components.avatar.SmallEmojiAvatar
import com.dertefter.design.components.poll.PollCard
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing

@Composable
fun PostCard(
    post: PostUiModel,
    modifier: Modifier = Modifier,
    onLike: () -> Unit = {},
    onUnlike: () -> Unit = {},
    onCommentsClick: () -> Unit = {},
    onRepostClick: () -> Unit = {},
    onUserClick: (userId: String) -> Unit = {},
    onVote: (optionIds: List<String>) -> Unit = {},
    onCopyLink: () -> Unit = {},
    onEdit: () -> Unit = {},
    onPin: () -> Unit = {},
    onDelete: () -> Unit = {},
    isOnMyWall: Boolean = false
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large)
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = MaterialTheme.spacing.large)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large)
            ) {
                SmallEmojiAvatar(emoji = post.author.avatar)
                Column(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .clickable(
                            onClick = { onUserClick(post.author.id) }
                        )
                        .weight(1f)
                ){
                    Text(
                        text = post.author.displayName,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "@${post.author.username}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                var showMenu by remember { mutableStateOf(false) }
                Box {
                    IconButton(
                        onClick = { showMenu = true }
                    ) {
                        Icon(
                            imageVector = Icons.MoreHoriz,
                            contentDescription = ""
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        shape = MaterialTheme.shapes.largeIncreased,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Копировать ссылку") },
                            onClick = {
                                onCopyLink()
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(Icons.ContentCopy, contentDescription = null)
                            }
                        )
                        if (post.isOwner) {
                            DropdownMenuItem(
                                text = { Text("Редактировать") },
                                onClick = {
                                    onEdit()
                                    showMenu = false
                                },
                                leadingIcon = {
                                    Icon(Icons.Edit, contentDescription = null)
                                }
                            )
                            post.isPinned?.let {
                                DropdownMenuItem(
                                    text = { Text(if (post.isPinned) "Открепить" else "Закрепить") },
                                    onClick = {
                                        onPin()
                                        showMenu = false
                                    },
                                    leadingIcon = {
                                        Icon(
                                            if (post.isPinned) Icons.KeepOff else Icons.Keep,
                                            contentDescription = null
                                        )
                                    }
                                )
                            }
                        }

                        if (post.isOwner || isOnMyWall) {
                            DropdownMenuItem(
                                text = { Text("Удалить") },
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
                                }
                            )
                        }
                    }
                }
            }
            if (post.content.isNotEmpty()) {
                Text(
                    modifier = Modifier
                        .padding(horizontal = MaterialTheme.spacing.large),
                    text = post.content,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            if (post.attachments.isNotEmpty()) {
                AttachmentsCarousel(
                    attachments = post.attachments,
                    itemShape = MaterialTheme.shapes.largeIncreased
                )
            }
            post.poll?.let { poll ->
                PollCard(
                    modifier = Modifier.padding(horizontal = MaterialTheme.spacing.large),
                    title = poll.title,
                    options = poll.options,
                    isMultipleChoice = poll.isMultipleChoice,
                    totalCount = poll.totalCount,
                    onVote = { optionIds ->
                        onVote(optionIds)
                    }
                )
            }
            post.originalPost?.let { originalPost ->
                OriginalPostCard(
                    originalPost = originalPost,
                    modifier = Modifier.padding(horizontal = MaterialTheme.spacing.large),
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MaterialTheme.spacing.large),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
                ){

                    LikeButton(
                        likes = post.likesCount,
                        isLiked = post.isLiked,
                        onClick = if (post.isLiked) { onUnlike } else { onLike }
                    )
                    CommentsButton(
                        comments = post.commentsCount,
                        onClick = onCommentsClick
                    )
                    RepostButton(
                        reposts = post.repostsCount,
                        isReposted = post.isReposted,
                        onClick = onRepostClick
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
                ){
                    post.dominantEmoji?.let{ dominantEmoji ->
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

@Preview(showBackground = true)
@Composable
fun PostCardPreview() {
    AppTheme {
        PostCard(
            post = PostUiModel(
                id = "1",
                content = "Hello, this is a sample post content!",
                author = AuthorUiModel(
                    id = "author1",
                    username = "johndoe",
                    displayName = "John Doe",
                    avatar = "😐"
                ),
                attachments = listOf(
                    AttachmentUiModel(
                        id = "1",
                        type = "image",
                        url = "https://picsum.photos/400/300"
                    )
                ),
                likesCount = 10,
                isLiked = false,
                commentsCount = 5,
                repostsCount = 2,
                isReposted = true,
                viewsCount = 100,
                dominantEmoji = "🦎",
                isPinned = false,
                isOwner = false,
                originalPost = null,
                poll = null,
            ),
            isOnMyWall = true
        )
    }
}
