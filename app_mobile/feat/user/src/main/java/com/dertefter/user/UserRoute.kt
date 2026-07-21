package com.dertefter.user

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dertefter.user.presentation.UserScreen

@Composable
fun UserRoute(
    userId: String,
    showBackButton: Boolean,
    viewModel: UserViewModel = hiltViewModel(),
) {
    val userUiState by viewModel.userUiState.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val paginators by viewModel.paginators.collectAsStateWithLifecycle()

    val uiStates = viewModel.tabs.associateWith { tab ->
        viewModel.uiStates[tab]!!.collectAsStateWithLifecycle().value
    }

    LaunchedEffect(userId) {
        viewModel.initWithUserId(userId)
    }

    UserScreen(
        userUiState = userUiState,
        selectedTab = selectedTab,
        uiStates = uiStates,
        paginators = paginators,
        showBackButton = showBackButton,
        onEvent = viewModel::onEvent
    )
}
