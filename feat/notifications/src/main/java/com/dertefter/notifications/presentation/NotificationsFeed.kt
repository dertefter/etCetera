package com.dertefter.notifications.presentation

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.dertefter.data.dto.notifications.NotificationDto
import com.dertefter.design.components.PullToRefreshIndicator
import com.dertefter.design.components.loading.AppLoadingIndicator
import com.dertefter.design.theme.spacing
import com.dertefter.notifications.presentation.component.NotificationCard
import androidx.compose.ui.res.stringResource
import com.dertefter.notifications.R
import com.jamal_aliev.paginator.MutableCursorPaginator
import com.jamal_aliev.paginator.compose.paginated
import com.jamal_aliev.paginator.compose.rememberPaginated
import com.jamal_aliev.paginator.extension.isErrorState
import com.jamal_aliev.paginator.extension.isProgressState
import com.jamal_aliev.paginator.page.PaginatorUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsFeed(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    scrollBehavior: TopAppBarScrollBehavior? = null,
    paginator: MutableCursorPaginator<NotificationDto>,
    uiState: PaginatorUiState<NotificationDto>,
    onEvent: (Event) -> Unit,
) {
    val listState = rememberLazyListState()
    val paged = paginator.rememberPaginated(state = listState)
    val pullToRefreshState = rememberPullToRefreshState()
    val isRefreshing =
        (uiState is PaginatorUiState.Content) && (uiState.prependState.isProgressState())

    PullToRefreshBox(
        modifier = modifier.fillMaxSize(),
        state = pullToRefreshState,
        isRefreshing = isRefreshing,
        onRefresh = { onEvent(Event.OnRefresh) },
        indicator = {
            PullToRefreshIndicator(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = contentPadding.calculateTopPadding()),
                state = pullToRefreshState,
                isRefreshing = isRefreshing
            )
        }) {
        AnimatedContent(
            targetState = uiState, contentKey = { it::class }, transitionSpec = {
            fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
        }, label = "notifications_feed_state", modifier = Modifier.fillMaxSize()
        ) { state ->
            when (state) {
                PaginatorUiState.Idle, is PaginatorUiState.Loading -> {
                    Box(
                        Modifier
                            .padding(contentPadding)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        AppLoadingIndicator()
                    }
                }

                is PaginatorUiState.Empty -> {
                    Box(
                        Modifier
                            .padding(contentPadding)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(stringResource(R.string.notifications_empty_state))
                    }
                }

                is PaginatorUiState.Error -> {
                    Box(
                        Modifier
                            .padding(contentPadding)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(stringResource(R.string.notifications_load_error, state.exception.message ?: ""))
                    }
                }

                is PaginatorUiState.Content -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .then(
                                if (scrollBehavior != null) Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
                                else Modifier
                            ),
                        contentPadding = contentPadding,
                        state = listState,
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
                    ) {
                        paginated(paged) {
                            itemsIndexed(
                                state.items, key = { _, item -> item.id }) { index, notification ->
                                NotificationCard(
                                    notification = notification,
                                    isFirst = index == 0,
                                    isLast = index == state.items.lastIndex,
                                    modifier = Modifier.padding(horizontal = MaterialTheme.spacing.defaultScreenPadding),
                                    onClick = {
                                        if (notification.type == "follow") {
                                            onEvent(Event.OnOpenUser(notification.actor.id))
                                        } else {
                                            notification.targetId?.let { targetId ->
                                                notification.targetType?.let { targetType ->
                                                    if (targetType == "post") {
                                                        onEvent(Event.OnOpenPost(targetId))
                                                    }
                                                }
                                            }
                                        }


                                    },
                                    onUserClick = {
                                        onEvent(Event.OnOpenUser(notification.actor.id))
                                    })
                            }

                            appendIndicator {
                                state.appendState?.let { appendState ->
                                    Box(
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (appendState.isProgressState()) {
                                            CircularProgressIndicator()
                                        } else if (appendState.isErrorState()) {
                                            Text(stringResource(R.string.notifications_append_error))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
