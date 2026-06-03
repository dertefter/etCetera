package com.dertefter.user.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dertefter.data.dto.feed.PostDto
import com.dertefter.design.components.loading.AppLoadingIndicator
import com.dertefter.design.components.post.PostCard
import com.dertefter.design.icons.Icons
import com.dertefter.user.R
import com.dertefter.user.presentation.mapper.toUiModel
import com.jamal_aliev.paginator.compose.PaginatedLazyListHolder
import com.jamal_aliev.paginator.compose.paginated
import com.jamal_aliev.paginator.extension.isErrorState
import com.jamal_aliev.paginator.extension.isProgressState
import com.jamal_aliev.paginator.page.PaginatorUiState

fun LazyListScope.feed(
    uiState: PaginatorUiState<PostDto>,
    paged: PaginatedLazyListHolder<*>,
    onEvent: (Event) -> Unit,
    pinnedPostId: String? = null,
    isMe: Boolean = false
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
                    Text(stringResource(R.string.user_loading_error, uiState.exception.message ?: ""))
                }
            }
        }

        is PaginatorUiState.Content -> {
            val pinnedPost = if (pinnedPostId != null) uiState.items.find { it.id == pinnedPostId } else null
            val displayItems = if (pinnedPost != null) {
                listOf(pinnedPost) + uiState.items.filter { it.id != pinnedPostId }
            } else {
                uiState.items
            }
            paginated(paged) {
                itemsIndexed(
                    displayItems,
                    key = { _, post -> post.id }) { index, post ->
                    val isPinned = post.id == pinnedPostId
                    Column {
                        if (isPinned) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Keep,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.outline
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.user_pinned_post),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                        PostCard(
                            post = post.toUiModel(isPinned = isPinned),
                            modifier = Modifier.padding(vertical = 16.dp),
                            onLike = { onEvent(Event.OnLike(post.id)) },
                            onUnlike = { onEvent(Event.OnUnlike(post.id)) },
                            onCommentsClick = { onEvent(Event.OnNavigateToComments(post.id)) },
                            isOnMyWall = isMe,
                            onUserClick = {
                                onEvent(Event.OnOpenUser(it))
                            },
                            onOpenPost = { onEvent(Event.OnOpenPost(it)) },
                            onAttachmentClick = { pos, urls ->
                                onEvent(
                                    Event.OnOpenAttachmentsViewer(
                                    urls = urls,
                                    position = pos
                                ))
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
