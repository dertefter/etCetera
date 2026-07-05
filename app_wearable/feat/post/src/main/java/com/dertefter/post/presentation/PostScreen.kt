package com.dertefter.post.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.foundation.lazy.TransformingLazyColumnState
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import com.dertefter.comments.CommentsViewModel
import com.dertefter.comments.presentation.CommentsFeed
import com.dertefter.data.dto.comments.CommentDto
import com.dertefter.design.components.common.TransformingListItem
import com.dertefter.design.components.post.AuthorUiModel
import com.dertefter.design.components.post.PostCard
import com.dertefter.design.components.post.PostUiModel
import com.dertefter.design.theme.WearableTheme
import com.dertefter.design.theme.spacing
import com.dertefter.post.R
import com.jamal_aliev.paginator.core.page.PaginatorUiState
import com.jamal_aliev.paginator.cursor.MutableCursorPaginator
import com.jamal_aliev.paginator.cursor.bookmark.CursorBookmark
import com.jamal_aliev.paginator.cursor.dsl.mutableCursorPaginator
import com.jamal_aliev.paginator.cursor.load.CursorLoadResult
import com.dertefter.comments.presentation.Event as CommentsEvent

@Composable
fun PostScreen(
    uiState: UiState,
    meUserId: String?,
    onEvent: (Event) -> Unit,
    commentsViewModel: CommentsViewModel = hiltViewModel()
) {
    val commentsUiState by if (uiState.post != null) {
        commentsViewModel.getUiState(uiState.post.id).collectAsStateWithLifecycle()
    } else {
        remember { mutableStateOf(PaginatorUiState.Idle) }
    }

    PostScreenContent(
        uiState = uiState,
        meUserId = meUserId,
        onEvent = onEvent,
        commentsUiState = commentsUiState,
        commentsPaginator = if (uiState.post != null) commentsViewModel.getPaginator(
            uiState.post.id,
        ) else null,
        onCommentsEvent = commentsViewModel::onEvent
    )
}

@Composable
fun PostScreenContent(
    uiState: UiState,
    meUserId: String?,
    onEvent: (Event) -> Unit,
    commentsUiState: PaginatorUiState<CommentDto>,
    commentsPaginator: MutableCursorPaginator<String, CommentDto>?,
    onCommentsEvent: (CommentsEvent) -> Unit,
) {
    val listState = remember(uiState.post?.id) {
        TransformingLazyColumnState()
    }

    uiState.post?.let { post ->
        if (commentsPaginator != null) {
            CommentsFeed(
                meUserId = meUserId,
                paginator = commentsPaginator,
                onEvent = onCommentsEvent,
                uiState = commentsUiState,
                listState = listState,
                header = { transformationSpec ->
                    item {
                        TransformingListItem(transformationSpec = transformationSpec) {
                            PostCard(
                                post = post,
                                onLike = { onEvent(Event.OnLike) },
                                onUnlike = { onEvent(Event.OnUnlike) },
                                onUserClick = { userId -> onEvent(Event.OnOpenUser(userId)) },
                                onVote = { optionIds -> onEvent(Event.OnVote(optionIds)) },
                                onOpenPost = { onEvent(Event.OnOpenPost(it)) },
                                onAttachmentClick = { attachments, position ->
                                    onEvent(
                                        Event.OnOpenAttachmentsViewer(
                                            attachments,
                                            position
                                        )
                                    )
                                },
                                onHashtagClick = {
                                    onEvent(Event.OnOpenHashtag(it))
                                },
                                showCommentsButton = false,
                                onDelete = { onEvent(Event.OnDeletePost(post.id)) },
                                onCommentsClick = {},
                                onPin = { onEvent(Event.OnPin(post.id)) },
                                onUnpin = { onEvent(Event.OnUnpin(post.id)) },
                                onRepostClick = { onEvent(Event.OnRepost(post.id)) }
                            )
                        }
                    }
                    item {
                        TransformingListItem(transformationSpec = transformationSpec) {
                            Text(
                                text = stringResource(R.string.post_comments),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .padding(top = MaterialTheme.spacing.large)
                                    .padding(bottom = MaterialTheme.spacing.medium)
                            )
                        }
                    }
                },
            )
        }
    }
}

@Preview(device = "id:wearos_large_round", showBackground = true)
@Composable
fun PostScreenPreview() {
    WearableTheme {
        val sampleAuthor = AuthorUiModel(
            id = "author1",
            username = "johndoe",
            displayName = "John Doe",
            avatar = "😐",
            hasNuksta = true,
            verified = true,
            pin = null
        )

        val samplePost = PostUiModel(
            id = "1",
            content = "This is a sample post content for the preview.",
            spans = emptyList(),
            author = sampleAuthor,
            attachments = emptyList(),
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
        )

        val sampleComments = listOf(
            CommentDto(
                id = "1",
                content = "This is a sample comment",
                author = com.dertefter.data.dto.feed.AuthorDto(
                    id = "1",
                    avatar = "😊",
                    username = "johndoe",
                    verified = true,
                    hasNuksta = false,
                    displayName = "John Doe"
                ),
                likesCount = 10,
                repliesCount = 2,
                isLiked = false,
                createdAt = "2023-10-27T10:00:00Z"
            )
        )

        val samplePaginator = mutableCursorPaginator {
            load {
                CursorLoadResult(
                    data = sampleComments,
                    bookmark = CursorBookmark(null, "initial", null)
                )
            }
        }

        val uiState = UiState(
            post = samplePost,
            isLoading = false
        )

        PostScreenContent(
            uiState = uiState,
            meUserId = "author1",
            onEvent = {},
            commentsUiState = PaginatorUiState.Content(
                prependState = null,
                items = sampleComments,
                appendState = null
            ),
            commentsPaginator = samplePaginator,
            onCommentsEvent = {}
        )
    }
}
