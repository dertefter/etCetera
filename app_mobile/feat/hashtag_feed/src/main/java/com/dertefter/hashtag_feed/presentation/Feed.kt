package com.dertefter.hashtag_feed.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dertefter.data.dto.feed.PostDto
import com.dertefter.design.components.loading.AppLoadingIndicator
import com.dertefter.design.components.post.PostCard
import com.dertefter.design.theme.spacing
import com.dertefter.hashtag_feed.R
import com.dertefter.hashtag_feed.presentation.mapper.toUiModel
import com.jamal_aliev.paginator.compose.cursor.PaginatedLazyListHolder
import com.jamal_aliev.paginator.compose.cursor.paginated
import com.jamal_aliev.paginator.core.extension.isErrorState
import com.jamal_aliev.paginator.core.extension.isProgressState
import com.jamal_aliev.paginator.core.page.PaginatorUiState

fun LazyListScope.feed(
    uiState: PaginatorUiState<PostDto>,
    paged: PaginatedLazyListHolder<*>,
    onEvent: (Event) -> Unit,
) {
    when (uiState) {
        is PaginatorUiState.Empty -> {
            item {
                Box(Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.user_no_posts))
                }
            }
        }

        else -> {
            val items = when (uiState) {
                is PaginatorUiState.Loading -> uiState.state.data
                is PaginatorUiState.Error -> uiState.state.data
                is PaginatorUiState.Content -> uiState.items
                PaginatorUiState.Idle -> emptyList()
            }

            paginated(paged) {
                postItems(items, onEvent)

                appendIndicator {
                    HashtagFeedAppendIndicator(uiState)
                }
            }
        }
    }
}

private fun LazyListScope.postItems(
    items: List<PostDto>,
    onEvent: (Event) -> Unit
) {
    itemsIndexed(items, key = { _, post -> "post_${post.id}" }) { _, post ->
        Column(Modifier.animateItem()) {
            PostCard(
                post = post.toUiModel(),
                modifier = Modifier
                    .padding(horizontal = MaterialTheme.spacing.defaultScreenPadding),
                onLike = { onEvent(Event.OnLike(post.id)) },
                onUnlike = { onEvent(Event.OnUnlike(post.id)) },
                onCommentsClick = { onEvent(Event.OnNavigateToComments(post.id)) },
                onUserClick = { onEvent(Event.OnOpenUser(it)) },
                onOpenPost = { onEvent(Event.OnOpenPost(it)) },
                onAttachmentClick = { attachments, position ->
                    onEvent(Event.OnOpenAttachmentsViewer(attachments, position))
                },
                onHashtagClick = {
                    onEvent(Event.OnOpenHashtag(it))
                },
                onDelete = { onEvent(Event.OnDeletePost(post.id)) },
                onPin = { onEvent(Event.OnPin(post.id)) },
                onUnpin = { onEvent(Event.OnUnpin(post.id)) },
                onEdit = { onEvent(Event.OnEditPost(it)) },
                onVote = { optionIds -> onEvent(Event.OnVote(post.id, optionIds)) },
                onRepostClick = { onEvent(Event.OnRepost(post.id)) }
            )
        }
    }
}

@Composable
private fun HashtagFeedAppendIndicator(state: PaginatorUiState<PostDto>) {
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
            is PaginatorUiState.Error -> Text(stringResource(R.string.user_loading_error, state.state.exception.message ?: ""))
            is PaginatorUiState.Content -> {
                state.appendState?.let { appendState ->
                    if (appendState.isProgressState()) {
                        AppLoadingIndicator()
                    } else if (appendState.isErrorState()) {
                        Text(stringResource(R.string.user_append_error))
                    }
                }
            }
            is PaginatorUiState.Idle -> AppLoadingIndicator()
            else -> {}
        }
    }
}
