package com.dertefter.hashtag_feed.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
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
import com.dertefter.design.components.common.ErrorLarge
import com.dertefter.design.components.loading.AppLoadingIndicator
import com.dertefter.design.icons.Icons
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.spacing
import com.jamal_aliev.paginator.core.extension.isProgressState
import com.jamal_aliev.paginator.core.page.PaginatorUiState
import com.jamal_aliev.paginator.cursor.MutableCursorPaginator
import com.jamal_aliev.paginator.compose.cursor.rememberPaginated
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds
import com.dertefter.design.R as DesignR

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

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            LargeFlexibleTopAppBar(
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    AppNavigationIcon(
                        icon = Icons.ArrowBack,
                        onClick = { onEvent(Event.OnNavigateBack) },
                        contentDescription = stringResource(DesignR.string.design_back_content_desc)
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
        PullToRefreshBox(
            modifier = Modifier
                .fillMaxSize(),
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
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    .fillMaxSize(),
                contentPadding = contentPadding,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large),
            ) {
                if (paged != null) {
                    feed(
                        uiState = uiState.uiState,
                        paged = paged,
                        onEvent = onEvent
                    )
                } else if (uiState.isLoading) {
                    item {
                        Box(
                            Modifier
                                .fillParentMaxSize(), contentAlignment = Alignment.Center
                        ) {
                            AppLoadingIndicator()
                        }
                    }
                } else if (uiState.error != null) {
                    item {
                        Box(
                            Modifier
                                .fillParentMaxSize(), contentAlignment = Alignment.Center
                        ) {
                            ErrorLarge(
                                onRetry = { onEvent(Event.OnRefresh) }
                            )
                        }
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
