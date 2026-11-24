package com.dertefter.messanger

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dertefter.messanger.presentation.ChatListScreen
import dev.zacsweers.metrox.viewmodel.metroViewModel

@Composable
fun ChatListRoute(
    showBackButton: Boolean,
    viewModel: ChatListViewModel = metroViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ChatListScreen(
        onEvent = viewModel::onEvent,
        uiState = uiState,
        paginator = viewModel.paginator,
        showBackButton = showBackButton
    )
}
