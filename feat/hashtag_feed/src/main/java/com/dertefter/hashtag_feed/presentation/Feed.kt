package com.dertefter.hashtag_feed.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dertefter.data.dto.feed.PostDto
import com.dertefter.design.components.loading.AppLoadingIndicator
import com.dertefter.design.components.post.PostCard
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
        PaginatorUiState.Idle -> {
            item {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    AppLoadingIndicator()
                }
            }
        }

        is PaginatorUiState.Loading -> {
            item {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    AppLoadingIndicator()
                }
            }
        }

        is PaginatorUiState.Empty -> {
            item {
                Box(Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.user_no_posts))
                }
            }
        }

        is PaginatorUiState.Error -> {
            item {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.user_loading_error, uiState.state.exception.message ?: ""))
                }
            }
        }

        is PaginatorUiState.Content -> {
            val displayItems =  uiState.items
            paginated(paged) {
                itemsIndexed(
                    displayItems,
                    key = { _, post -> post.id }) { index, post ->
                    Column {
                        PostCard(
                            post = post.toUiModel(),
                            modifier = Modifier.padding(vertical = 16.dp),
                            onLike = { onEvent(Event.OnLike(post.id)) },
                            onUnlike = { onEvent(Event.OnUnlike(post.id)) },
                            onCommentsClick = { onEvent(Event.OnNavigateToComments(post.id)) },
                            onUserClick = {
                                onEvent(Event.OnOpenUser(it))
                            },
                            onOpenPost = { onEvent(Event.OnOpenPost(it)) },
                            onAttachmentClick = { attachments, position ->
                                onEvent(Event.OnOpenAttachmentsViewer(attachments, position))
                            }
                        )
                    }
                    if (index < displayItems.lastIndex) {
                        HorizontalDivider()
                    }
                }

                appendIndicator {
                    uiState.appendState?.let { appendState ->
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (appendState.isProgressState()) {
                                CircularProgressIndicator()
                            } else if (appendState.isErrorState()) {
                                Text(stringResource(R.string.user_append_error))
                            }
                        }
                    }
                }
            }
        }
    }
}
