package com.dertefter.notifications.presentation

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.stringResource
import com.dertefter.data.dto.notifications.NotificationDto
import com.dertefter.design.components.PullToRefreshIndicator
import com.dertefter.design.components.buttons.AppNavigationIcon
import com.dertefter.design.theme.rounding
import com.dertefter.design.theme.spacing
import com.dertefter.notifications.R
import com.jamal_aliev.paginator.core.extension.isProgressState
import com.jamal_aliev.paginator.core.page.PaginatorUiState
import com.jamal_aliev.paginator.cursor.MutableCursorPaginator

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NotificationsScreen(
    onEvent: (Event) -> Unit,
    uiState: PaginatorUiState<NotificationDto>,
    paginator: MutableCursorPaginator<String, NotificationDto>,
    selectedFilter: String? = null,
    showBackButton: Boolean,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val pullToRefreshState = rememberPullToRefreshState()
    val isRefreshing = (uiState is PaginatorUiState.Content<*>) && (uiState.prependState.isProgressState())

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
            topBar = {
                val containerColor = lerp(
                    MaterialTheme.colorScheme.surface,
                    MaterialTheme.colorScheme.surfaceContainer,
                    scrollBehavior.state.overlappedFraction
                )
                Surface(color = containerColor) {
                    Column {
                        LargeFlexibleTopAppBar(
                            navigationIcon = {
                                if (showBackButton){
                                    AppNavigationIcon(
                                        onClick = {
                                            onEvent(Event.OnNavigateBack)
                                        }
                                    )
                                }
                            },
                            title = {
                                Text(stringResource(R.string.notifications_title))
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color.Transparent,
                                scrolledContainerColor = Color.Transparent
                            ),
                            scrollBehavior = scrollBehavior,
                        )

                        NotificationFilters(
                            selectedFilter = selectedFilter,
                            onFilterClick = { type ->
                                onEvent(Event.OnFilterChanged(type))
                            }
                        )
                    }
                }
            }
        ) { padding ->
            NotificationsFeed(
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = padding,
                paginator = paginator,
                uiState = uiState,
                onEvent = onEvent,
                scrollBehavior = scrollBehavior
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationFilters(
    selectedFilter: String?,
    onFilterClick: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val filters = listOf(
        null to R.string.filter_all,
        "follow" to R.string.filter_follow,
        "like" to R.string.filter_like,
        "comment" to R.string.filter_comment,
        "repost" to R.string.filter_repost
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = MaterialTheme.spacing.defaultScreenPadding + MaterialTheme.rounding.largeIncreased),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
    ) {
        filters.forEach { (type, labelRes) ->
            FilterChip(
                selected = selectedFilter == type,
                onClick = { onFilterClick(type) },
                label = { Text(stringResource(labelRes)) }
            )
        }
    }
}
