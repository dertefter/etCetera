package com.dertefter.feed.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dertefter.data.common.Constants
import com.dertefter.data.dto.feed.PostDto
import com.dertefter.design.components.loading.AppLoadingIndicator
import com.dertefter.design.components.post.PostCard
import com.dertefter.design.theme.spacing
import com.dertefter.feed.R
import com.dertefter.feed.presentation.mapper.toUiModel
import com.jamal_aliev.paginator.compose.cursor.PaginatedLazyStaggeredGridScope
import com.jamal_aliev.paginator.compose.cursor.itemsIndexed
import com.jamal_aliev.paginator.compose.cursor.paginated
import com.jamal_aliev.paginator.compose.cursor.rememberPaginated
import com.jamal_aliev.paginator.core.extension.isErrorState
import com.jamal_aliev.paginator.core.extension.isProgressState
import com.jamal_aliev.paginator.core.page.PaginatorUiState
import com.jamal_aliev.paginator.cursor.MutableCursorPaginator
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Feed(
    paginator: MutableCursorPaginator<String, PostDto>,
    onEvent: (Event) -> Unit,
    uiState: PaginatorUiState<PostDto>,
    contentPadding: PaddingValues = PaddingValues(),
    scrollBehavior: TopAppBarScrollBehavior? = null,
    gridState: LazyStaggeredGridState = rememberLazyStaggeredGridState()
) {
    val paged = paginator.rememberPaginated(state = gridState)

    LaunchedEffect(gridState) {
        while (true) {
            delay(Constants.STATS_UPDATE_DELAY_MS.milliseconds)
            val visibleItems = gridState.layoutInfo.visibleItemsInfo
            val visibleIds = visibleItems.mapNotNull {
                it.key.toString().takeIf { it.startsWith("post_") }?.removePrefix("post_")
            }
            if (visibleIds.isNotEmpty()) {
                onEvent(Event.OnUpdateStats(visibleIds))
            }
        }
    }

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
        label = "feed_state",
        modifier = Modifier.fillMaxSize()
    ) { state ->
        when (state) {

            else -> {
                val items = when (state) {
                    is PaginatorUiState.Loading -> state.state.data
                    is PaginatorUiState.Idle -> emptyList()
                    is PaginatorUiState.Empty -> emptyList()
                    is PaginatorUiState.Error -> state.state.data
                    is PaginatorUiState.Content -> state.items
                }

                LazyVerticalStaggeredGrid (
                    modifier = Modifier
                        .padding(horizontal = MaterialTheme.spacing.defaultScreenPadding)
                        .fillMaxSize()
                        .then(
                            if (scrollBehavior != null) Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
                            else Modifier
                        ),
                    state = gridState,
                    contentPadding = contentPadding,
                    columns = StaggeredGridCells.Adaptive(minSize = 500.dp),
                    verticalItemSpacing = MaterialTheme.spacing.large,
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
                ) {
                    paginated(paged) {

                        postItems(items, onEvent)

                        appendIndicator {
                            FeedAppendIndicator(state)
                        }
                    }
                }
            }
        }
    }
}

private fun PaginatedLazyStaggeredGridScope.postItems(
    items: List<PostDto>,
    onEvent: (Event) -> Unit
) {
    itemsIndexed<Any, PostDto>(items, key = { _, post -> "post_${post.id}" }) { _, post ->
        PostCard(
            post = post.toUiModel(),
            modifier = Modifier
                .animateItem()
                .fillMaxWidth(),
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
            onEdit = { onEvent(Event.OnEditPost(it)) },
            onRepostClick = { onEvent(Event.OnRepost(post.id)) }
        )
    }
}

@Composable
private fun FeedAppendIndicator(state: PaginatorUiState<PostDto>) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            is PaginatorUiState.Loading -> {
                AppLoadingIndicator()
            }
            is PaginatorUiState.Error -> Text(stringResource(R.string.feed_error_loading))
            is PaginatorUiState.Content -> {
                state.appendState?.let { appendState ->
                    if (appendState.isProgressState()) {
                        AppLoadingIndicator()
                    } else if (appendState.isErrorState()) {
                        Text(stringResource(R.string.feed_error_loading))
                    }
                }
            }
            is PaginatorUiState.Idle -> {
                AppLoadingIndicator()
            }
            else -> {}
        }
    }
}
