package com.dertefter.messanger.chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.zacsweers.metrox.viewmodel.metroViewModel

@Composable
fun ChatRoute(
    chatId: String,
    viewModel: ChatViewModel = metroViewModel()
) {
    val uiState by viewModel.getUiState(chatId).collectAsStateWithLifecycle()
    val meUserId by viewModel.meUserId.collectAsStateWithLifecycle()
    val paginator = viewModel.getPaginator(chatId)

    ChatScreen(
        chatId = chatId,
        uiState = uiState,
        paginator = paginator,
        meUserId = meUserId,
        onEvent = { event -> viewModel.onEvent(event, chatId) }
    )
}
