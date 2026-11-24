package com.dertefter.messanger.presentation

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import com.dertefter.data.dto.messenger.ChatDto
import com.dertefter.design.components.loading.AppLoadingIndicator
import com.dertefter.design.theme.spacing
import com.dertefter.messanger.R
import com.dertefter.messanger.presentation.component.ChatCard
import com.jamal_aliev.paginator.compose.cursor.paginated
import com.jamal_aliev.paginator.compose.cursor.rememberPaginated
import com.jamal_aliev.paginator.core.extension.isErrorState
import com.jamal_aliev.paginator.core.extension.isProgressState
import com.jamal_aliev.paginator.core.page.PaginatorUiState
import com.jamal_aliev.paginator.cursor.MutableCursorPaginator

@Composable
fun ChatsFeed(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    scrollBehavior: TopAppBarScrollBehavior? = null,
    paginator: MutableCursorPaginator<String, ChatDto>,
    uiState: PaginatorUiState<ChatDto>,
    onEvent: (Event) -> Unit,
) {
    val listState = rememberSaveable(paginator, saver = LazyListState.Saver) { LazyListState() }
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
        label = "chats_feed_state",
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
                    Text(stringResource(R.string.chats_empty_state))
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
                        chatItems(items, onEvent)

                        appendIndicator {
                            ChatAppendIndicator(state)
                        }
                    }
                }
            }
        }
    }
}

private fun LazyListScope.chatItems(
    items: List<ChatDto>,
    onEvent: (Event) -> Unit
) {
    itemsIndexed(
        items, key = { _, item -> item.chatId }) { index, chat ->
        ChatCard(
            chat = chat,
            index = index,
            count = items.count(),
            modifier = Modifier.padding(horizontal = MaterialTheme.spacing.defaultScreenPadding),
            onClick = {
                onEvent(Event.OnOpenChat(chat.chatId))
            }
        )
    }
}

@Composable
private fun ChatAppendIndicator(state: PaginatorUiState<ChatDto>) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(MaterialTheme.spacing.extraLarge),
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            is PaginatorUiState.Loading -> AppLoadingIndicator()
            is PaginatorUiState.Error -> Text(
                stringResource(
                    R.string.chats_load_error, state.state.exception.message ?: ""
                )
            )

            is PaginatorUiState.Content -> {
                state.appendState?.let { appendState ->
                    if (appendState.isProgressState()) {
                        AppLoadingIndicator()
                    } else if (appendState.isErrorState()) {
                        Text(stringResource(R.string.chats_append_error))
                    }
                }
            }

            else -> {}
        }
    }
}
