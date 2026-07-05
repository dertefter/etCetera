package com.dertefter.user.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.TransformingLazyColumnItemScope
import androidx.wear.compose.foundation.lazy.TransformingLazyColumnScope
import androidx.wear.compose.foundation.lazy.TransformingLazyColumnState
import androidx.wear.compose.foundation.lazy.itemsIndexed
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.lazy.TransformationSpec
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import com.dertefter.data.dto.feed.PostDto
import com.dertefter.design.components.common.TransformingListItem
import com.dertefter.design.components.post.PostCard
import com.dertefter.design.components.post.PostCardShimmer
import com.dertefter.design.theme.spacing
import com.dertefter.user.R
import com.dertefter.user.presentation.mapper.toUiModel
import com.jamal_aliev.paginator.core.extension.isErrorState
import com.jamal_aliev.paginator.core.extension.isProgressState
import com.jamal_aliev.paginator.core.page.PaginatorUiState
import com.jamal_aliev.paginator.cursor.MutableCursorPaginator
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun Feed(
    paginator: MutableCursorPaginator<String, PostDto>,
    onEvent: (Event) -> Unit,
    uiState: PaginatorUiState<PostDto>,
    isMe: Boolean = false,
    listState: TransformingLazyColumnState = rememberTransformingLazyColumnState(),
    header: (TransformingLazyColumnScope.(TransformationSpec) -> Unit)? = null
) {
    val transformationSpec = rememberTransformationSpec()
    val currentUiState by rememberUpdatedState(uiState)

    LaunchedEffect(listState) {
        while (true) {
            delay(2000.milliseconds)
            val visibleItems = listState.layoutInfo.visibleItems
            val visibleIds = visibleItems.map { it.key.toString() }
            onEvent(Event.OnUpdateStats(visibleIds))
        }
    }

    LaunchedEffect(listState, paginator) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            layoutInfo.visibleItems.lastOrNull()?.index to layoutInfo.totalItemsCount
        }.collect { (lastIndex, totalItemsCount) ->
            val state = currentUiState
            if (lastIndex != null && state is PaginatorUiState.Content && totalItemsCount > 2) {
                if (lastIndex >= totalItemsCount - 2) {
                    val appendState = state.appendState
                    if (appendState == null || (!appendState.isProgressState() && !appendState.isErrorState())) {
                        paginator.goNextPage()
                    }
                }
            }
        }
    }

    val items = when (uiState) {
        is PaginatorUiState.Loading -> uiState.state.data
        is PaginatorUiState.Idle -> emptyList()
        is PaginatorUiState.Error -> uiState.state.data
        is PaginatorUiState.Content -> uiState.items
        else -> emptyList()
    }

    ScreenScaffold(
        scrollState = listState,
        contentPadding = PaddingValues(
            top = 48.dp,
            start = MaterialTheme.spacing.defaultScreenPadding,
            end = MaterialTheme.spacing.defaultScreenPadding,
            bottom = 48.dp
        )
    ) { contentPadding ->
        TransformingLazyColumn(
            Modifier.fillMaxSize(),
            state = listState,
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large)
        ) {
            header?.invoke(this, transformationSpec)
            postItems(items, onEvent, transformationSpec, isMe)
            item(key = "user_feed_append_indicator") {
                FeedAppendIndicator(uiState, transformationSpec)
            }
        }
    }
}

private fun TransformingLazyColumnScope.postItems(
    items: List<PostDto>,
    onEvent: (Event) -> Unit,
    transformationSpec: TransformationSpec,
    isMe: Boolean
) {
    itemsIndexed(items, key = { _, post -> post.id }) { _, post ->
        TransformingListItem(transformationSpec = transformationSpec) {
            PostCard(
                post = post.toUiModel(),
                onLike = { onEvent(Event.OnLike(post.id)) },
                onUnlike = { onEvent(Event.OnUnlike(post.id)) },
                onCommentsClick = { onEvent(Event.OnNavigateToComments(post.id)) },
                onUserClick = { userId -> onEvent(Event.OnOpenUser(userId)) },
                onVote = { optionIds -> onEvent(Event.OnVote(post.id, optionIds)) },
                onOpenPost = { postId -> onEvent(Event.OnOpenPost(postId)) },
                onAttachmentClick = { attachments, position ->
                    onEvent(Event.OnOpenAttachmentsViewer(attachments, position))
                },
                onDelete = { onEvent(Event.OnDeletePost(post.id)) },
                onHashtagClick = {
                    onEvent(
                        Event.OnOpenHashtag(it)
                    )
                },
                onPin = { onEvent(Event.OnPin(post.id)) },
                onUnpin = { onEvent(Event.OnUnpin(post.id)) },
                isOnMyWall = isMe,
                onRepostClick = { onEvent(Event.OnRepost(post.id)) }
            )
        }
    }
}

@Composable
private fun TransformingLazyColumnItemScope.FeedAppendIndicator(
    state: PaginatorUiState<PostDto>,
    transformationSpec: TransformationSpec
) {
    when (state) {
        is PaginatorUiState.Loading -> {
            TransformingListItem(transformationSpec = transformationSpec) {
                PostCardShimmer()
            }
            TransformingListItem(transformationSpec = transformationSpec) {
                PostCardShimmer()
            }
            TransformingListItem(transformationSpec = transformationSpec) {
                PostCardShimmer()
            }

        }

        is PaginatorUiState.Error -> {
            TransformingListItem(transformationSpec = transformationSpec) {
                Text(stringResource(R.string.user_append_error))
            }

        }
        is PaginatorUiState.Content -> {
            state.appendState?.let { appendState ->
                if (appendState.isProgressState()) {

                    TransformingListItem(transformationSpec = transformationSpec) {
                        PostCardShimmer()
                    }
                    TransformingListItem(transformationSpec = transformationSpec) {
                        PostCardShimmer()
                    }
                    TransformingListItem(transformationSpec = transformationSpec) {
                        PostCardShimmer()
                    }

                } else if (appendState.isErrorState()) {
                    TransformingListItem(transformationSpec = transformationSpec) {
                        Text(stringResource(R.string.user_append_error))
                    }

                }
            }
        }

        is PaginatorUiState.Idle -> {
            TransformingListItem(transformationSpec = transformationSpec) {
                PostCardShimmer()
            }
            TransformingListItem(transformationSpec = transformationSpec) {
                PostCardShimmer()
            }
            TransformingListItem(transformationSpec = transformationSpec) {
                PostCardShimmer()
            }
        }

        else -> {}
    }
}
