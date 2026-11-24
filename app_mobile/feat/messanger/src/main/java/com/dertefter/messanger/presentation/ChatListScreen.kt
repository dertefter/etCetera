package com.dertefter.messanger.presentation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.plus
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.dertefter.data.dto.messenger.ChatDto
import com.dertefter.data.dto.messenger.LastMessageDto
import com.dertefter.design.components.PullToRefreshIndicator
import com.dertefter.design.components.appbar.AppTopBar
import com.dertefter.design.components.buttons.AppNavigationButton
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.bottomNavHeight
import com.dertefter.messanger.R
import com.jamal_aliev.paginator.core.page.PaginatorUiState
import com.jamal_aliev.paginator.cursor.MutableCursorPaginator
import com.jamal_aliev.paginator.cursor.bookmark.CursorBookmark
import com.jamal_aliev.paginator.cursor.dsl.mutableCursorPaginator
import com.jamal_aliev.paginator.cursor.load.CursorLoadResult
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@Composable
fun ChatListScreen(
    onEvent: (Event) -> Unit,
    uiState: PaginatorUiState<ChatDto>,
    paginator: MutableCursorPaginator<String, ChatDto>,
    showBackButton: Boolean,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val pullToRefreshState = rememberPullToRefreshState()
    val isRefreshing = (uiState is PaginatorUiState.Loading<*> || uiState == PaginatorUiState.Idle)

    val hazeState = rememberHazeState()

    PullToRefreshBox(
        modifier = Modifier.fillMaxSize(),
        state = pullToRefreshState,
        isRefreshing = isRefreshing,
        onRefresh = {
            onEvent(Event.OnRefresh)
        },
        indicator = {
            PullToRefreshIndicator(
                modifier = Modifier.align(Alignment.TopCenter),
                state = pullToRefreshState,
                isRefreshing = isRefreshing
            )
        }
    ) {
        Scaffold(
            topBar = {
                AppTopBar(
                    hazeState = hazeState,
                    navigationIcon = {
                        if (showBackButton) {
                            AppNavigationButton(
                                onClick = {
                                    onEvent(Event.OnNavigateBack)
                                }
                            )
                        }
                    },
                    title = {
                        Text(stringResource(R.string.chats_title))
                    },
                    scrollBehavior = scrollBehavior
                )
            }
        ) { contentPadding ->
            ChatsFeed(
                modifier = Modifier
                    .fillMaxSize()
                    .hazeSource(hazeState),
                contentPadding = contentPadding + PaddingValues(bottom = MaterialTheme.bottomNavHeight),
                paginator = paginator,
                uiState = uiState,
                onEvent = onEvent,
                scrollBehavior = scrollBehavior
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatListScreenPreview() {
    AppTheme {
        val sampleChats = listOf(
            ChatDto(
                chatId = "1",
                title = "Кто тут?",
                avatar = "👾",
                unreadCount = 0,
                lastMessage = LastMessageDto(
                    id = "msg1",
                    text = "Привет!"
                )
            ),
            ChatDto(
                chatId = "2",
                title = "ИТД",
                avatar = "🤖",
                unreadCount = 2,
                lastMessage = LastMessageDto(
                    id = "msg2",
                    text = "Новая акция!"
                )
            )
        )

        val samplePaginator = remember {
            mutableCursorPaginator {
                load {
                    CursorLoadResult(
                        data = sampleChats,
                        bookmark = CursorBookmark(null, "initial", null)
                    )
                }
            }
        }
        val sampleUiState = PaginatorUiState.Content(
            prependState = null,
            items = sampleChats,
            appendState = null
        )

        ChatListScreen(
            onEvent = {},
            uiState = sampleUiState,
            paginator = samplePaginator,
            showBackButton = true
        )
    }
}
