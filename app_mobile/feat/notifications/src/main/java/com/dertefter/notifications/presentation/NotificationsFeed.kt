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
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dertefter.data.dto.notifications.NotificationDto
import com.dertefter.design.components.loading.AppLoadingIndicator
import com.dertefter.design.theme.spacing
import com.dertefter.notifications.R
import com.dertefter.notifications.presentation.component.NotificationCard
import com.jamal_aliev.paginator.compose.cursor.paginated
import com.jamal_aliev.paginator.compose.cursor.rememberPaginated
import com.jamal_aliev.paginator.core.extension.isErrorState
import com.jamal_aliev.paginator.core.extension.isProgressState
import com.jamal_aliev.paginator.core.page.PaginatorUiState
import com.jamal_aliev.paginator.cursor.MutableCursorPaginator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsFeed(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    scrollBehavior: TopAppBarScrollBehavior? = null,
    paginator: MutableCursorPaginator<String, NotificationDto>,
    uiState: PaginatorUiState<NotificationDto>,
    onEvent: (Event) -> Unit,
) {
    val listState = androidx.compose.runtime.remember(paginator) { LazyListState() }
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
        label = "notifications_feed_state",
        modifier = modifier.fillMaxSize()
    ) { state ->
        when (state) {
            PaginatorUiState.Idle -> {
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

            else -> {
                val items = when (state) {
                    is PaginatorUiState.Loading -> state.state.data
                    is PaginatorUiState.Error -> state.state.data
                    is PaginatorUiState.Content -> state.items
                }

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
                        notificationItems(items, onEvent)

                        appendIndicator {
                            NotificationAppendIndicator(state)
                        }
                    }
                }
            }
        }
    }
}

private fun LazyListScope.notificationItems(
    items: List<NotificationDto>,
    onEvent: (Event) -> Unit
) {
    itemsIndexed(
        items, key = { _, item -> item.id }) { index, notification ->
        NotificationCard(
            notification = notification,
            isFirst = index == 0,
            isLast = index == items.lastIndex,
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
}

@Composable
private fun NotificationAppendIndicator(state: PaginatorUiState<NotificationDto>) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            is PaginatorUiState.Loading -> CircularProgressIndicator()
            is PaginatorUiState.Error -> Text(
                stringResource(
                    R.string.notifications_load_error, state.state.exception.message ?: ""
                )
            )

            is PaginatorUiState.Content -> {
                state.appendState?.let { appendState ->
                    if (appendState.isProgressState()) {
                        CircularProgressIndicator()
                    } else if (appendState.isErrorState()) {
                        Text(stringResource(R.string.notifications_append_error))
                    }
                }
            }

            else -> {}
        }
    }
}
