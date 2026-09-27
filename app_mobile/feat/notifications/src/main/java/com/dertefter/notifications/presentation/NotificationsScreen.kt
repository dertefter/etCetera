package com.dertefter.notifications.presentation

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.dertefter.data.dto.notifications.NotificationDto
import com.dertefter.design.components.PullToRefreshIndicator
import com.dertefter.design.components.appbar.AppTopBar
import com.dertefter.design.components.buttons.AppNavigationIcon
import com.dertefter.design.theme.bottomNavHeight
import com.dertefter.design.theme.rounding
import com.dertefter.design.theme.spacing
import com.dertefter.notifications.R
import com.jamal_aliev.paginator.core.page.PaginatorUiState
import com.jamal_aliev.paginator.cursor.MutableCursorPaginator
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

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
    val isRefreshing = (uiState is PaginatorUiState.Loading<*> || uiState == PaginatorUiState.Idle)

    val horizontalExtraSpace = MaterialTheme.rounding.largeIncreased * (1f - scrollBehavior.state.overlappedFraction)

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
                    scrollBehavior = scrollBehavior,
                    supportingContent = {
                        NotificationFilters(
                            horizontalExtraSpace = horizontalExtraSpace,
                            selectedFilter = selectedFilter,
                            onFilterClick = { type ->
                                onEvent(Event.OnFilterChanged(type))
                            }
                        )
                    }
                )
            }
        ) { contentPadding ->
            NotificationsFeed(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationFilters(
    selectedFilter: String?,
    onFilterClick: (String?) -> Unit,
    modifier: Modifier = Modifier,
    horizontalExtraSpace: Dp = MaterialTheme.rounding.largeIncreased
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
            .padding(horizontal = MaterialTheme.spacing.defaultScreenPadding + horizontalExtraSpace),
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
