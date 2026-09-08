package com.dertefter.settings_theme

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.dertefter.settings_theme.presentation.screens.PostsThemeScreen

@Composable
fun PostsThemeRoute(
    viewModel: SettingsThemeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    AnimatedVisibility(
        visible = uiState != null
    ) {
        uiState?.let { uiState ->
            PostsThemeScreen(
                uiState = uiState,
                onEvent = viewModel::onEvent
            )
        }
    }
}
