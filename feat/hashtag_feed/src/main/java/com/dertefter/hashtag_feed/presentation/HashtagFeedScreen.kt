package com.dertefter.hashtag_feed.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.dertefter.data.dto.feed.PostDto
import com.dertefter.design.components.PullToRefreshIndicator
import com.dertefter.design.components.buttons.AppNavigationIcon
import com.dertefter.design.components.loading.AppLoadingIndicator
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing
import com.jamal_aliev.paginator.compose.cursor.rememberPaginated
import com.jamal_aliev.paginator.core.extension.isProgressState
import com.jamal_aliev.paginator.core.page.PaginatorUiState
import com.jamal_aliev.paginator.cursor.MutableCursorPaginator
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class, ExperimentalFoundationApi::class)
@Composable
fun HashtagFeedScreen(
    onEvent: (Event) -> Unit,
    uiState: UiState,
    paginator: MutableCursorPaginator<String, PostDto>? = null
) {
    val lazyListState = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val paged = paginator?.rememberPaginated(state = lazyListState)
    val isRefreshing = (uiState.uiState is PaginatorUiState.Content<PostDto> && uiState.uiState.prependState.isProgressState())
    val pullToRefreshState = rememberPullToRefreshState()

    LaunchedEffect(lazyListState) {
        delay(2000.milliseconds)
        while (true) {
            val visibleItems = lazyListState.layoutInfo.visibleItemsInfo
            val visibleIds = visibleItems.mapNotNull {
                it.key.toString().takeIf { key -> key.length > 5 }
            }
            if (visibleIds.isNotEmpty()) {
                onEvent(Event.OnUpdateStats(visibleIds))
            }
            delay(5000.milliseconds)
        }
    }

    PullToRefreshBox(
        modifier = Modifier.fillMaxSize(),
        state = pullToRefreshState,
        isRefreshing = isRefreshing,
        onRefresh = { onEvent(Event.OnRefresh) },
        indicator = {
            PullToRefreshIndicator(
                modifier = Modifier.align(Alignment.TopCenter),
                state = pullToRefreshState,
                isRefreshing = isRefreshing
            )
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                LargeFlexibleTopAppBar(
                    scrollBehavior = scrollBehavior,
                    navigationIcon = {
                        AppNavigationIcon(
                            icon = Icons.ArrowBack,
                            onClick = { onEvent(Event.OnNavigateBack) },
                        )
                    },
                    title = {
                        Text(
                            text = uiState.hashtag?.let { "#$it" } ?: "",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors().copy(
                        titleContentColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        ) { contentPadding ->
            HashtagFeedContent(
                uiState = uiState.uiState,
                contentPadding = contentPadding,
                listState = lazyListState,
                onEvent = onEvent,
                paged = paged,
                scrollBehavior = scrollBehavior
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HashtagFeedContent(
    uiState: PaginatorUiState<PostDto>,
    contentPadding: PaddingValues,
    listState: LazyListState,
    onEvent: (Event) -> Unit,
    paged: com.jamal_aliev.paginator.compose.cursor.PaginatedLazyListHolder<*>?,
    scrollBehavior: androidx.compose.material3.TopAppBarScrollBehavior
) {
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
        label = "hashtag_feed_state",
        modifier = Modifier.fillMaxSize()
    ) { state ->
        when (state) {
            PaginatorUiState.Idle -> {
                Box(Modifier.padding(contentPadding).fillMaxSize(), contentAlignment = Alignment.Center) {
                    AppLoadingIndicator()
                }
            }

            is PaginatorUiState.Empty -> {
                Box(Modifier.padding(contentPadding).fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(com.dertefter.hashtag_feed.R.string.user_no_posts))
                }
            }

            else -> {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .nestedScroll(scrollBehavior.nestedScrollConnection)
                        .fillMaxSize(),
                    contentPadding = contentPadding,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large),
                ) {
                    if (paged != null) {
                        feed(
                            uiState = state,
                            paged = paged,
                            onEvent = onEvent
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HashtagFeedScreenPreview() {
    AppTheme {
        HashtagFeedScreen(
            onEvent = {},
            uiState = UiState(
                hashtag = "test",
                isLoading = false
            ),
        )
    }
}
