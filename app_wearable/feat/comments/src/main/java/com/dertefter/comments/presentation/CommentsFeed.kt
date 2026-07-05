package com.dertefter.comments.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
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
import com.dertefter.comments.R
import com.dertefter.design.components.comment.CommentCard
import com.dertefter.design.components.comment.CommentCardShimmer
import com.dertefter.design.components.comment.CommentsLoadingShimmer
import com.dertefter.comments.presentation.mapper.toUiModel
import com.dertefter.data.dto.comments.CommentDto
import com.dertefter.data.dto.feed.AuthorDto
import com.dertefter.design.components.common.TransformingListItem
import com.dertefter.design.theme.WearableTheme
import com.dertefter.design.theme.spacing
import com.jamal_aliev.paginator.core.extension.isErrorState
import com.jamal_aliev.paginator.core.extension.isProgressState
import com.jamal_aliev.paginator.core.page.PaginatorUiState
import com.jamal_aliev.paginator.cursor.MutableCursorPaginator
import com.jamal_aliev.paginator.cursor.bookmark.CursorBookmark
import com.jamal_aliev.paginator.cursor.dsl.mutableCursorPaginator
import com.jamal_aliev.paginator.cursor.load.CursorLoadResult

@Composable
fun CommentsFeed(
    paginator: MutableCursorPaginator<String, CommentDto>,
    onEvent: (Event) -> Unit,
    uiState: PaginatorUiState<CommentDto>,
    listState: TransformingLazyColumnState = rememberTransformingLazyColumnState(),
    header: (TransformingLazyColumnScope.(TransformationSpec) -> Unit)? = null,
    meUserId: String?
) {
    val transformationSpec = rememberTransformationSpec()
    val currentUiState by rememberUpdatedState(uiState)

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
                        runCatching { paginator.goNextPage() }
                    }
                }
            }
        }
    }

    val items = remember(uiState) {
        val rawItems = when (uiState) {
            is PaginatorUiState.Loading -> uiState.state.data
            is PaginatorUiState.Idle -> emptyList()
            is PaginatorUiState.Empty -> emptyList()
            is PaginatorUiState.Error -> uiState.state.data
            is PaginatorUiState.Content -> uiState.items
        }
        rawItems.distinctBy { it.id }
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
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
        ) {
            header?.invoke(this, transformationSpec)

            if (uiState is PaginatorUiState.Idle) {
                item {
                    TransformingListItem(transformationSpec = transformationSpec) {
                        CommentsLoadingShimmer()
                    }
                }
            } else if (uiState is PaginatorUiState.Empty) {
                item {
                    TransformingListItem(transformationSpec = transformationSpec) {
                        Box(
                            Modifier
                                .padding(MaterialTheme.spacing.large)
                                .fillMaxSize(), contentAlignment = Alignment.Center
                        ) {
                            Text(stringResource(R.string.comments_empty))
                        }
                    }
                }
            } else {
                itemsIndexed(items, key = { _, comment -> comment.id }) { _, comment ->
                    TransformingListItem(transformationSpec = transformationSpec) {
                        CommentCard(
                            meUserId = meUserId,
                            comment = comment.toUiModel(meUserId),
                            onLike = { onEvent(Event.OnLike(it)) },
                            onUnlike = { onEvent(Event.OnUnlike(it)) },
                            onLoadMoreReplies = { onEvent(Event.OnLoadMoreReplies(it)) },
                            onUserClick = { onEvent(Event.OnOpenUser(it)) },
                            onDelete = { onEvent(Event.OnDeleteComment(it)) }
                        )
                    }
                }

                item(key = "append_indicator") {
                    CommentAppendIndicator(uiState, transformationSpec)
                }
            }
        }
    }
}

@Composable
private fun TransformingLazyColumnItemScope.CommentAppendIndicator(
    state: PaginatorUiState<CommentDto>,
    transformationSpec: TransformationSpec
) {
    Box(
        Modifier
            .fillMaxWidth()
            .transformedHeight(transformationSpec::getTransformedHeight)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            is PaginatorUiState.Loading -> CommentCardShimmer()
            is PaginatorUiState.Error -> Text(stringResource(R.string.comments_failed_to_load))
            is PaginatorUiState.Content -> {
                state.appendState?.let { appendState ->
                    if (appendState.isProgressState()) {
                        CommentCardShimmer()
                    } else if (appendState.isErrorState()) {
                        Text(stringResource(R.string.comments_failed_to_load))
                    }
                }
            }
            is PaginatorUiState.Idle -> CommentCardShimmer()
            else -> {}
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CommentsFeedPreview() {
    WearableTheme {
        val sampleAuthor = AuthorDto(
            id = "1",
            avatar = "😊",
            username = "johndoe",
            verified = true,
            hasNuksta = false,
            displayName = "John Doe"
        )

        val sampleComments = listOf(
            CommentDto(
                id = "1",
                content = "This is a sample comment",
                author = sampleAuthor,
                likesCount = 10,
                repliesCount = 2,
                isLiked = false,
                createdAt = "2023-10-27T10:00:00Z"
            ),
            CommentDto(
                id = "2",
                content = "Another sample comment with some more text to see how it looks when it wraps to multiple lines in the preview.",
                author = sampleAuthor,
                likesCount = 5,
                repliesCount = 0,
                isLiked = true,
                createdAt = "2023-10-27T11:00:00Z"
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

        val uiState = PaginatorUiState.Content(
            prependState = null,
            items = sampleComments,
            appendState = null
        )

        CommentsFeed(
            paginator = samplePaginator,
            onEvent = {},
            uiState = uiState,
            meUserId = "",
        )
    }
}
