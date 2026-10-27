package com.dertefter.comments.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dertefter.comments.R
import com.dertefter.comments.presentation.mapper.toUiModel
import com.dertefter.data.dto.comments.CommentDto
import com.dertefter.design.components.comment.CommentCard
import com.dertefter.design.components.loading.AppLoadingIndicator
import com.dertefter.design.theme.spacing
import com.jamal_aliev.paginator.compose.cursor.PaginatedLazyListScope
import com.jamal_aliev.paginator.compose.cursor.paginated
import com.jamal_aliev.paginator.compose.cursor.rememberPaginated
import com.jamal_aliev.paginator.core.extension.isErrorState
import com.jamal_aliev.paginator.core.extension.isProgressState
import com.jamal_aliev.paginator.core.page.PaginatorUiState
import com.jamal_aliev.paginator.cursor.MutableCursorPaginator

@Composable
fun CommentsFeed(
    modifier: Modifier = Modifier,
    paginator: MutableCursorPaginator<String, CommentDto>,
    onEvent: (Event) -> Unit,
    uiState: PaginatorUiState<CommentDto>,
    contentPadding: PaddingValues = PaddingValues(),
    scrollBehavior: TopAppBarScrollBehavior? = null,
    header: (LazyListScope.() -> Unit)? = null,
    meUserId: String?
) {
    val listState = rememberLazyListState()
    val paged = paginator.rememberPaginated(state = listState)

    AnimatedContent(
        targetState = uiState,
        contentKey = {
            when (it) {
                PaginatorUiState.Idle -> 0
                is PaginatorUiState.Empty -> 1
                else -> 2
            }
        },
        transitionSpec = {
            fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
        },
        label = "comments_feed_state",
        modifier = modifier
            .fillMaxSize()
    ) { state ->
        when (state) {
            PaginatorUiState.Idle -> {
                Box(
                    Modifier
                        .padding(MaterialTheme.spacing.large)
                        .padding(contentPadding)
                        .fillMaxSize(), contentAlignment = Alignment.TopCenter
                ) {
                    AppLoadingIndicator()
                }
            }

            is PaginatorUiState.Empty -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .then(
                            if (scrollBehavior != null) Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
                            else Modifier
                        ),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large),
                    state = listState,
                    contentPadding = contentPadding
                ) {
                    header?.invoke(this)
                    item {
                        Box(
                            Modifier
                                .fillMaxSize(), contentAlignment = Alignment.TopCenter
                        ) {
                            Text(stringResource(R.string.comments_empty))
                        }
                    }
                }
            }

            else -> {
                val items = when (state) {
                    is PaginatorUiState.Loading -> state.state.data
                    is PaginatorUiState.Error -> state.state.data
                    is PaginatorUiState.Content -> state.items
                }
                // Deduplicated once per page set: `count` is read by every row, so counting
                // inside the row factory would make composition O(n^2).
                val visibleItems = remember(items) { items.distinctBy { it.id } }
                // Aliased so that the parameter is not shadowed by the DSL `header { }` slot.
                val headerScope = header

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .then(
                            if (scrollBehavior != null) Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
                            else Modifier
                        ),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                    state = listState,
                    contentPadding = contentPadding
                ) {
                    paginated(paged) {
                        headerScope?.invoke(HeaderLazyListScope(this))
                        header {
                            Spacer(
                                modifier = Modifier.height(MaterialTheme.spacing.small)
                            )
                        }

                        itemsIndexed(
                            visibleItems,
                            key = { _, comment -> comment.id }) { index, comment ->
                            Column(Modifier.animateItem()) {
                                CommentCard(
                                    modifier = Modifier.padding(horizontal = MaterialTheme.spacing.defaultScreenPadding),
                                    index = index,
                                    count = visibleItems.size,
                                    onAttachmentClick = {attachments, position ->
                                        onEvent(Event.OnOpenAttachmentsViewer(attachments, position))
                                    },
                                    meUserId = meUserId,
                                    comment = comment.toUiModel(meUserId),
                                    onLike = { onEvent(Event.OnLike(it)) },
                                    onUnlike = { onEvent(Event.OnUnlike(it)) },
                                    onLoadMoreReplies = { onEvent(Event.OnLoadMoreReplies(it)) },
                                    onUserClick = { onEvent(Event.OnOpenUser(it)) },
                                    onReplyClick = { commentId, userId ->
                                        onEvent(
                                            Event.OnReply(
                                                commentId, userId
                                            )
                                        )
                                    },
                                    onDelete = { commentId ->
                                        onEvent(
                                            Event.OnDeleteComment(commentId)
                                        )

                                    },
                                    onReport = { commentId ->
                                        onEvent(
                                            Event.OnReport(commentId)
                                        )
                                    }
                                )
                            }
                        }

                        appendIndicator {
                            CommentAppendIndicator(state)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Routes the caller's header items through [PaginatedLazyListScope.header] so the paginator also
 * counts them: the DSL only tallies what the list actually rendered, and a header placed outside
 * the `paginated { }` block would shift every data index by its item count.
 */
private class HeaderLazyListScope(
    private val scope: PaginatedLazyListScope,
) : LazyListScope by scope {

    override fun item(
        key: Any?,
        contentType: Any?,
        content: @Composable LazyItemScope.() -> Unit,
    ) {
        scope.header(key = key, contentType = contentType, content = content)
    }

    override fun items(
        count: Int,
        key: ((index: Int) -> Any)?,
        contentType: (index: Int) -> Any?,
        itemContent: @Composable LazyItemScope.(index: Int) -> Unit,
    ) {
        repeat(count) { index ->
            scope.header(key = key?.invoke(index), contentType = contentType(index)) {
                itemContent(index)
            }
        }
    }
}

@Composable
private fun CommentAppendIndicator(state: PaginatorUiState<CommentDto>) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            is PaginatorUiState.Loading -> AppLoadingIndicator()
            is PaginatorUiState.Error -> Text(stringResource(R.string.comments_failed_to_load))
            is PaginatorUiState.Content -> {
                state.appendState?.let { appendState ->
                    if (appendState.isProgressState()) {
                        AppLoadingIndicator()
                    } else if (appendState.isErrorState()) {
                        Text(stringResource(R.string.comments_failed_to_load))
                    }
                }
            }

            is PaginatorUiState.Idle -> AppLoadingIndicator()

            else -> {}
        }
    }
}
