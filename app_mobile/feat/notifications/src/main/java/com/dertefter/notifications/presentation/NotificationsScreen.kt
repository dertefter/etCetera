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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ChipShapes
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dertefter.data.dto.notifications.ActorDto
import com.dertefter.data.dto.notifications.NotificationDto
import com.dertefter.design.components.PullToRefreshIndicator
import com.dertefter.design.components.appbar.AppTopBar
import com.dertefter.design.components.buttons.AppNavigationIcon
import com.dertefter.design.theme.AppTheme
import com.dertefter.design.theme.bottomNavHeight
import com.dertefter.design.theme.spacing
import com.dertefter.notifications.R
import com.jamal_aliev.paginator.core.page.PaginatorUiState
import com.jamal_aliev.paginator.cursor.MutableCursorPaginator
import com.jamal_aliev.paginator.cursor.bookmark.CursorBookmark
import com.jamal_aliev.paginator.cursor.dsl.mutableCursorPaginator
import com.jamal_aliev.paginator.cursor.load.CursorLoadResult
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

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
                            horizontalExtraSpace = MaterialTheme.spacing.defaultScreenPadding,
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

@Composable
fun NotificationFilters(
    selectedFilter: String?,
    onFilterClick: (String?) -> Unit,
    modifier: Modifier = Modifier,
    horizontalExtraSpace: Dp = 0.dp
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
            .padding(horizontal = horizontalExtraSpace),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
    ) {
        filters.forEach { (type, labelRes) ->

            val isSelected = selectedFilter == type


            FilterChip(
                selected = isSelected,
                onClick = { onFilterClick(type) },
                label = { Text(stringResource(labelRes)) },
                shapes = ChipShapes(
                    shape = MaterialTheme.shapes.small,
                    selectedShape = CircleShape,
                    pressedShape = CircleShape,
                ),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    labelColor = MaterialTheme.colorScheme.onSurface,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationsScreenPreview() {
    AppTheme {
        val sampleNotifications = listOf(
            NotificationDto(
                id = "1",
                type = "like",
                preview = "liked your post",
                createdAt = "2023-10-27T12:00:00Z",
                read = false,
                count = 1,
                actor = ActorDto(
                    id = "actor1",
                    displayName = "John Doe",
                    username = "johndoe",
                    avatar = "",
                    isFollowing = true,
                    isFollowedBy = false
                )
            ),
            NotificationDto(
                id = "2",
                type = "follow",
                preview = "started following you",
                createdAt = "2023-10-27T11:30:00Z",
                read = true,
                count = 1,
                actor = ActorDto(
                    id = "actor2",
                    displayName = "Jane Smith",
                    username = "janesmith",
                    avatar = "",
                    isFollowing = false,
                    isFollowedBy = true
                )
            ),
            NotificationDto(
                id = "3",
                type = "comment",
                preview = "commented on your post",
                createdAt = "2023-10-27T10:15:00Z",
                read = true,
                count = 3,
                actor = ActorDto(
                    id = "actor3",
                    displayName = "Alex Johnson",
                    username = "alexj",
                    avatar = "",
                    isFollowing = true,
                    isFollowedBy = true
                )
            )
        )

        val samplePaginator = remember {
            mutableCursorPaginator {
                load {
                    CursorLoadResult(
                        data = sampleNotifications,
                        bookmark = CursorBookmark(null, "initial", null)
                    )
                }
            }
        }
        val sampleUiState = PaginatorUiState.Content(
            prependState = null,
            items = sampleNotifications,
            appendState = null
        )

        NotificationsScreen(
            onEvent = {},
            uiState = sampleUiState,
            paginator = samplePaginator,
            selectedFilter = null,
            showBackButton = true
        )
    }
}
