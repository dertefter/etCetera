package com.dertefter.followers

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.dertefter.data.dto.followers.FollowerUserDto
import com.dertefter.followers.presentation.Event
import com.dertefter.followers.presentation.FollowersScreen
import com.dertefter.followers.presentation.Tab
import com.jamal_aliev.paginator.core.page.PaginatorUiState
import com.jamal_aliev.paginator.cursor.MutableCursorPaginator

@Composable
fun FollowersRoute(
    userId: String,
    startTabIsFollowing: Boolean,
    viewModel: FollowersViewModel = hiltViewModel(),
) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    LaunchedEffect(userId, startTabIsFollowing) {
        viewModel.init(userId, startTabIsFollowing)
    }
    LaunchedEffect(Unit) {
        viewModel.onEvent(Event.OnRefresh(selectedTab))
    }

    val uiStates = viewModel.uiStates.mapValues { it.value.collectAsState(PaginatorUiState.Idle).value }

    val paginators = viewModel.tabs.associateWith { viewModel.getPaginator(it) }

    if (paginators.values.all { it != null }) {
        FollowersScreen(
            onEvent = viewModel::onEvent,
            selectedTab = selectedTab,
            uiStates = uiStates,
            paginators = paginators as Map<Tab, MutableCursorPaginator<String, FollowerUserDto>>
        )
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}
