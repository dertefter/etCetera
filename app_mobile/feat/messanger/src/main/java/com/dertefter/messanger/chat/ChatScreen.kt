package com.dertefter.messanger.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.tooling.preview.Preview
import com.dertefter.design.theme.AppTheme
import com.jamal_aliev.paginator.cursor.bookmark.CursorBookmark
import com.jamal_aliev.paginator.cursor.dsl.mutableCursorPaginator
import com.jamal_aliev.paginator.cursor.load.CursorLoadResult
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.dertefter.data.dto.messenger.MessageDto
import com.dertefter.design.components.PullToRefreshIndicator
import com.dertefter.design.components.appbar.AppTopBar
import com.dertefter.design.components.appbar.AppTopBarStyle
import com.dertefter.design.components.buttons.AppNavigationButton
import com.dertefter.design.components.loading.AppLoadingIndicator
import com.dertefter.design.theme.bottomNavHeight
import com.dertefter.design.theme.spacing
import com.jamal_aliev.paginator.compose.cursor.paginated
import com.jamal_aliev.paginator.compose.cursor.rememberPaginated
import com.jamal_aliev.paginator.core.page.PaginatorUiState
import com.jamal_aliev.paginator.cursor.MutableCursorPaginator
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@Composable
fun ChatScreen(
    chatId: String,
    uiState: PaginatorUiState<MessageDto>,
    paginator: MutableCursorPaginator<String, MessageDto>,
    meUserId: String?,
    onEvent: (ChatEvent) -> Unit,
) {

    val pullToRefreshState = rememberPullToRefreshState()
    val isRefreshing = (uiState is PaginatorUiState.Loading<*> || uiState == PaginatorUiState.Idle)
    val hazeState = rememberHazeState()

    val listState = rememberSaveable(paginator, saver = LazyListState.Saver) { LazyListState() }

    PullToRefreshBox(
        modifier = Modifier.fillMaxSize(),
        state = pullToRefreshState,
        isRefreshing = isRefreshing,
        onRefresh = {
            onEvent(ChatEvent.OnRefresh)
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

                Row(
                    modifier = Modifier
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.background,
                                    MaterialTheme.colorScheme.background.copy(alpha = 0f)
                                )
                            )
                        )
                        .statusBarsPadding()
                        .padding(
                            all = MaterialTheme.spacing.medium
                        )
                        .fillMaxWidth()
                ){
                    AppNavigationButton(
                        hazeState = hazeState,
                        onClick = {
                            onEvent(ChatEvent.OnNavigateBack)
                        }
                    )
                }


            }
        ) { contentPadding ->
            AnimatedContentState(
                hazeState =hazeState,
                uiState = uiState,
                contentPadding = contentPadding,
                paginator = paginator,
                meUserId = meUserId,
                listState = listState,
                onEvent = onEvent
            )
        }
    }
}

@Composable
private fun AnimatedContentState(
    hazeState: HazeState,
    uiState: PaginatorUiState<MessageDto>,
    contentPadding: PaddingValues,
    paginator: MutableCursorPaginator<String, MessageDto>,
    meUserId: String?,
    listState: LazyListState,
    onEvent: (ChatEvent) -> Unit
) {
    val paged = paginator.rememberPaginated(state = listState)

    when (uiState) {
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
                Text("No messages yet")
            }
        }

        else -> {
            val items = when (uiState) {
                is PaginatorUiState.Loading -> uiState.state.data
                is PaginatorUiState.Error -> uiState.state.data
                is PaginatorUiState.Content -> uiState.items
            }

            LazyColumn(
                modifier = Modifier
                    .hazeSource(hazeState)
                    .fillMaxSize(),
                contentPadding = contentPadding + PaddingValues(bottom = MaterialTheme.bottomNavHeight),
                state = listState,
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                reverseLayout = true
            ) {
                paginated(paged) {
                    itemsIndexed(
                        items,
                        key = { _, item -> item.messageId }
                    ) { _, message ->
                        MessageCard(
                            message = message,
                            meUserId = meUserId,
                            modifier = Modifier.padding(horizontal = MaterialTheme.spacing.defaultScreenPadding),
                            onAttachmentClick = { attachments, position ->
                                onEvent(ChatEvent.OnOpenAttachmentsViewer(attachments, position))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    AppTheme {
        val sampleMessages = listOf(
            MessageDto(
                messageId = "1",
                senderId = "user1",
                text = "Hello! How are you doing today?",
                createdAt = "2023-10-27T10:00:00Z"
            ),
            MessageDto(
                messageId = "2",
                senderId = "me",
                text = "Hi! I'm doing great, working on Jetpack Compose previews and UI design.",
                createdAt = "2023-10-27T10:01:00Z"
            ),
            MessageDto(
                messageId = "3",
                senderId = "user1",
                text = "That sounds awesome! How is it going so far?",
                createdAt = "2023-10-27T10:02:00Z"
            ),
            MessageDto(
                messageId = "4",
                senderId = "me",
                text = "Everything looks super clean and pixel-perfect!",
                createdAt = "2023-10-27T10:03:00Z"
            ),
            MessageDto(
                messageId = "5",
                senderId = "user1",
                text = "Awesome! Let me know if you need any help with anything else.",
                createdAt = "2023-10-27T10:04:00Z"
            ),
            MessageDto(
                messageId = "6",
                senderId = "me",
                text = "Will do, thanks!",
                createdAt = "2023-10-27T10:05:00Z"
            )
        )

        val samplePaginator = remember {
            mutableCursorPaginator {
                load {
                    CursorLoadResult(
                        data = sampleMessages,
                        bookmark = CursorBookmark(null, "initial", null)
                    )
                }
            }
        }
        val sampleUiState = PaginatorUiState.Content(
            prependState = null,
            items = sampleMessages,
            appendState = null
        )

        ChatScreen(
            chatId = "chat1",
            uiState = sampleUiState,
            paginator = samplePaginator,
            meUserId = "me",
            onEvent = {}
        )
    }
}
