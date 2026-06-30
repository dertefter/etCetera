package com.dertefter.feed.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dertefter.data.dto.feed.PostDto
import com.dertefter.design.components.post.FeedLoadingShimmer
import com.dertefter.design.components.post.PostCard
import com.dertefter.feed.R
import com.dertefter.feed.presentation.mapper.toUiModel
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
    listState: LazyListState = rememberLazyListState()
) {
    val paged = paginator.rememberPaginated(state = listState)

    LaunchedEffect(listState) {
        while (true) {
            delay(2000.milliseconds)
            val visibleItems = listState.layoutInfo.visibleItemsInfo
            val visibleIds = visibleItems.map { it.key.toString() }
            onEvent(Event.OnUpdateStats(visibleIds))
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

                LazyColumn(
                    Modifier
                        .fillMaxSize()
                        .then(
                            if (scrollBehavior != null) Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
                            else Modifier
                        ),
                    state = listState,
                    contentPadding = contentPadding
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

private fun LazyListScope.postItems(
    items: List<PostDto>,
    onEvent: (Event) -> Unit
) {
    itemsIndexed(items, key = { _, post -> post.id }) { index, post ->
        Column(Modifier.animateItem()) {
            PostCard(
                post = post.toUiModel(),
                modifier = Modifier.padding(vertical = 16.dp),
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
                onRepostClick = { onEvent(Event.OnRepost(post.id)) }
            )
            if (index < items.lastIndex) HorizontalDivider()
        }
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
                FeedLoadingShimmer()
            }
            is PaginatorUiState.Error -> Text(stringResource(R.string.feed_error_loading))
            is PaginatorUiState.Content -> {
                state.appendState?.let { appendState ->
                    if (appendState.isProgressState()) {
                        FeedLoadingShimmer()
                    } else if (appendState.isErrorState()) {
                        Text(stringResource(R.string.feed_error_loading))
                    }
                }
            }
            is PaginatorUiState.Idle -> {
                FeedLoadingShimmer()
            }
            else -> {}
        }
    }
}
