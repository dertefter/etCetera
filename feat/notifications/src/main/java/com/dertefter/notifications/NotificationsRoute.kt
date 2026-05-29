package com.dertefter.notifications

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dertefter.notifications.presentation.Event
import com.dertefter.notifications.presentation.NotificationsScreen

@Composable
fun NotificationsRoute(
    viewModel: NotificationsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onEvent(Event.OnRefresh)
    }

    NotificationsScreen(
        onEvent = viewModel::onEvent,
        uiState = uiState,
        paginator = viewModel.paginator
    )
}
