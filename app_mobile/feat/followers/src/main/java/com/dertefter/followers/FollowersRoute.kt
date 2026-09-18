package com.dertefter.followers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dertefter.followers.presentation.Event
import com.dertefter.followers.presentation.FollowersScreen
import com.dertefter.followers.presentation.Tab

@Composable
fun FollowersRoute(
    userId: String,
    startTabIsFollowing: Boolean,
    viewModel: FollowersViewModel = hiltViewModel(),
) {

    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()

    LaunchedEffect(userId, startTabIsFollowing) {
        viewModel.init(userId, startTabIsFollowing)
    }
    LaunchedEffect(Unit) {
        viewModel.onEvent(Event.OnRefresh(selectedTab))
    }

    val followersUiState by viewModel.uiStates[Tab.FOLLOWERS]!!.collectAsStateWithLifecycle()
    val followingUiState by viewModel.uiStates[Tab.FOLLOWING]!!.collectAsStateWithLifecycle()

    val uiStates = mapOf(
        Tab.FOLLOWERS to followersUiState,
        Tab.FOLLOWING to followingUiState
    )

    val paginators = viewModel.tabs.associateWith { viewModel.getPaginator(it) }

    FollowersScreen(
        onEvent = viewModel::onEvent,
        selectedTab = selectedTab,
        uiStates = uiStates,
        paginators = paginators
    )
}
