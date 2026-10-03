package com.dertefter.followers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dertefter.followers.presentation.FollowersScreen
import com.dertefter.navigation.Routes

@Composable
fun FollowersRoute(
    route: Routes.Followers,
    viewModel: FollowersViewModel = hiltViewModel(),
) {
    remember(route) {
        viewModel.init(route.userId, route.startTabIsFollowing)
        true
    }

    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    FollowersScreen(
        onEvent = viewModel::onEvent,
        userId = route.userId,
        selectedTab = selectedTab,
        uiState = uiState,
        paginator = viewModel.getPaginator(selectedTab)
    )
}
