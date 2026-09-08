package com.dertefter.settings_theme

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.dertefter.settings_theme.presentation.screens.EmojiAvatarsScreen

@Composable
fun EmojiAvatarsRoute(
    viewModel: SettingsThemeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    AnimatedVisibility(
        visible = uiState != null
    ) {
        uiState?.let { uiState ->
            EmojiAvatarsScreen(
                uiState = uiState,
                onEvent = viewModel::onEvent
            )
        }
    }
}
