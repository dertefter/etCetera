package com.dertefter.notifications

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import dev.zacsweers.metrox.viewmodel.metroViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dertefter.notifications.presentation.NotificationsScreen

@Composable
fun NotificationsRoute(
    showBackButton: Boolean,
    viewModel: NotificationsViewModel = metroViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()

    NotificationsScreen(
        onEvent = viewModel::onEvent,
        uiState = uiState,
        paginator = viewModel.paginator,
        selectedFilter = selectedFilter,
        showBackButton = showBackButton
    )
}
