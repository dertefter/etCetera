package com.dertefter.followers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.dertefter.followers.presentation.Event
import com.dertefter.followers.presentation.FollowersScreen
import com.jamal_aliev.paginator.core.page.PaginatorUiState

@Composable
fun FollowersRoute(
    viewModel: FollowersViewModel = hiltViewModel(),
) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.onEvent(Event.OnRefresh(selectedTab))
    }

    val uiStates = viewModel.uiStates.mapValues { it.value.collectAsState(PaginatorUiState.Idle).value }

    val paginators = viewModel.tabs.associateWith { viewModel.getPaginator(it) }

    FollowersScreen(
        onEvent = viewModel::onEvent,
        selectedTab = selectedTab,
        uiStates = uiStates,
        paginators = paginators
    )
}
