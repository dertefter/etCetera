package com.dertefter.followers.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.wear.compose.foundation.lazy.TransformingLazyColumnState
import androidx.wear.compose.material3.CircularProgressIndicator
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import com.dertefter.data.dto.followers.FollowerUserDto
import com.dertefter.design.components.common.TransformingListItem
import com.dertefter.design.theme.spacing
import com.dertefter.followers.R
import com.jamal_aliev.paginator.core.page.PaginatorUiState
import com.jamal_aliev.paginator.cursor.MutableCursorPaginator

@Composable
fun FollowersScreen(
    onEvent: (Event) -> Unit,
    userId: String?,
    selectedTab: Tab,
    uiState: PaginatorUiState<FollowerUserDto>,
    paginator: MutableCursorPaginator<String, FollowerUserDto>?
) {
    // Use remember(userId, selectedTab) without rememberSaveable to ensure 
    // the list always starts at the top when switching users or tabs.
    val listState = remember(userId, selectedTab) {
        TransformingLazyColumnState()
    }

    LaunchedEffect(userId, selectedTab) {
        listState.scrollToItem(0)
    }

    val items = when (uiState) {
        is PaginatorUiState.Loading -> uiState.state.data
        is PaginatorUiState.Content -> uiState.items
        is PaginatorUiState.Error -> uiState.state.data
        else -> emptyList()
    }

    if (paginator == null || (items.isEmpty() && (uiState is PaginatorUiState.Loading || uiState is PaginatorUiState.Idle))) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Feed(
        paginator = paginator,
        onEvent = onEvent,
        uiState = uiState,
        listState = listState,
        header = { transformationSpec ->
            item {
                TransformingListItem(transformationSpec = transformationSpec) {
                    val text = when (selectedTab) {
                        Tab.FOLLOWERS -> stringResource(R.string.followers_tab_followers)
                        Tab.FOLLOWING -> stringResource(R.string.followers_tab_following)
                    }
                    Text(
                        text = text,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .padding(bottom = MaterialTheme.spacing.large)
                    )
                }
            }
        }
    )
}
