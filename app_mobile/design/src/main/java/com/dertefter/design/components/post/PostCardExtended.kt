package com.dertefter.design.components.post

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dertefter.design.components.avatar.Avatar
import com.dertefter.design.components.avatar.DisplayName
import com.dertefter.design.components.poll.PollCard
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.postContained
import com.dertefter.design.theme.spacing
import com.dertefter.design.transition.sharedElementByKey

@Composable
fun PostCardExtended(
    post: PostUiModel,
    modifier: Modifier = Modifier,
    sharedKeyAppend: String = "",
    onLike: (postId: String) -> Unit = {},
    onUnlike: (postId: String) -> Unit = {},
    onCommentsClick: (postId: String) -> Unit = {},
    onRepostClick: (postId: String) -> Unit = {},
    onUserClick: (userId: String) -> Unit,
    onVote: (postId: String, optionIds: List<String>) -> Unit,
    showCommentsButton: Boolean = true,
    onOpenPost: (String) -> Unit,
    onHashtagClick: (hashtagId: String) -> Unit,
    onLinkClick: ((url: String) -> Unit)? = null,
    onAttachmentClick: (attachments: List<AttachmentUiModel>, position: Int) -> Unit,
) {
    val uriHandler = LocalUriHandler.current
    val finalOnLinkClick = onLinkClick ?: { url -> uriHandler.openUri(url) }

    val containerPadding = 0.dp

    val sharedAvatarId = "avatar_${post.author.id}$sharedKeyAppend"

    val sharedCardId = "card_${post.id}$sharedKeyAppend"

    val sharedNameId = "display_name_${post.author.id}$sharedKeyAppend"

    val sharedUsernameId = "username_${post.author.id}$sharedKeyAppend"

    Box(
        modifier = modifier
            .sharedElementByKey(sharedCardId)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large)
        ) {

            Column(
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Avatar(
                        modifier = Modifier
                            .sharedElementByKey(sharedAvatarId),
                        data = post.author.avatar,
                        containerSize = 76.dp,
                        onClick = {
                            onUserClick(post.author.id)
                        }
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        post.dominantEmoji?.let { dominantEmoji ->
                            DominantEmoji(
                                dominantEmoji = dominantEmoji,
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }

                        Views(
                            views = post.viewsCount,
                            isViewed = post.isViewed,
                            style = PostActionButtonStyle.LARGE
                        )



                    }



                }


                Column(
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.medium)
                        .clickable(
                            onClick = {
                                onUserClick(post.author.id)
                            }
                        )
                ) {
                    DisplayName(
                        modifier = Modifier
                            .sharedElementByKey(sharedNameId)
                            .fillMaxWidth(),
                        name = post.author.displayName,
                        verified = post.author.verified,
                        hasNuksta = post.author.hasNuksta,
                        textStyle = MaterialTheme.typography.titleLargeEmphasized,
                        pin = post.author.pin,
                    )

                    Text(
                        modifier = Modifier
                            .sharedElementByKey(sharedUsernameId),
                        text = "@${post.author.username}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    PrettyDate(
                        createdDate = post.getCreatedAtDate(),
                        editedDate = post.getEditedAtDate(),
                        textStyle = MaterialTheme.typography.bodyLarge,
                    )

                }
            }

            if (post.content.isNotEmpty()) {
                var revealedSpoilers by remember { mutableStateOf(setOf<Int>()) }
                val annotatedString = buildPostAnnotatedString(post.content, post.spans, revealedSpoilers)
                var layoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
                SelectionContainer {
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
                        onTextLayout = { layoutResult = it },
                        style = MaterialTheme.typography.headlineSmall
                    )
                }

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
                    modifier = Modifier
                        .padding(horizontal = containerPadding),
                    title = poll.title,
                    options = poll.options,
                    isMultipleChoice = poll.isMultipleChoice,
                    totalCount = poll.totalCount,
                    onVote = { optionIds ->
                        onVote(post.id, optionIds)
                    })
            }
            post.originalPost?.let { originalPost ->
                OriginalPostCard(
                    modifier = Modifier,
                    originalPost = originalPost,
                    onOpenPost = { origId -> onOpenPost(origId) },
                    onHashtagClick = onHashtagClick,
                    onUserClick = onUserClick,
                    onLike = { onLike(originalPost.id) },
                    onUnlike = { onUnlike(originalPost.id) },
                    onLinkClick = finalOnLinkClick,
                    onAttachmentClick = { attachments, position ->
                        onAttachmentClick(attachments, position)
                    },
                    onCommentsClick = {
                        onCommentsClick(originalPost.id)
                    },
                    onRepostClick = {
                        onRepostClick(originalPost.id)
                    },
                    onVote = { postId, optionIds ->
                        onVote(postId, optionIds)
                    }
                )

            }
            Row(
                modifier = Modifier
                    .padding(
                        end = containerPadding,
                        start = containerPadding,
                        bottom = containerPadding
                    )
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
                ) {

                    LikeButton(
                        modifier = Modifier.weight(1f),
                        likes = post.likesCount,
                        isLiked = post.isLiked,
                        onClick = {
                            if (post.isLiked) {
                                onUnlike(post.id)
                            } else {
                                onLike(post.id)
                            }
                        },
                        style = PostActionButtonStyle.LARGE
                    )
                    if (showCommentsButton){
                        CommentsButton(
                            modifier = Modifier.weight(1f),
                            comments = post.commentsCount, onClick = {
                                onCommentsClick(post.id)
                            },
                            style = PostActionButtonStyle.LARGE
                        )
                    }
                    RepostButton(
                        modifier = Modifier.weight(1f),
                        reposts = post.repostsCount,
                        isReposted = post.isReposted,
                        onClick = {
                            onRepostClick(post.id)
                        },
                        style = PostActionButtonStyle.LARGE
                    )
                }
            }
            AnimatedVisibility(
                visible = !MaterialTheme.postContained
            ) {
                HorizontalDivider(
                    modifier = Modifier.padding(bottom = MaterialTheme.spacing.medium)
                )
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun PostCardExtendedPreview() {
    AppTheme(
        postContained = true
    ) {
        PostCardExtended(
            post = PostUiModel(
                id = "1",
                content = "#супермиликотики",
                spans = emptyList(),
                author = AuthorUiModel(
                    id = "author1", username = "johndoeуууууууууууууууууууууууууууууууууууууууу", displayName = "John Doeуууууууууууууу", avatar = "😐", hasNuksta = true, verified = true, pin = null
                ),
                attachments = listOf(
                    AttachmentUiModel(
                        id = "1", type = "image", url = "https://picsum.photos/400/300"
                    )
                ),
                poll = null,
                likesCount = 10,
                isLiked = false,
                commentsCount = 5,
                repostsCount = 2,
                isReposted = true,
                isViewed = true,
                viewsCount = 100,
                dominantEmoji = "🦎",
                isPinned = true,
                isOwner = false,
                createdAt = "2026-08-05T12:00:00Z",
                editedAt = null,
                originalPost = null
            ),  onHashtagClick = {},
            onAttachmentClick = { _, _ -> },
            onOpenPost = {},
            onCommentsClick = {},
            onVote = {_,_ ->},
            onLike = {},
            onUnlike = {},
            onUserClick = {},
            onRepostClick = {},
        )
    }
}
