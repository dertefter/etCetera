package com.dertefter.settings_ai

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import dev.zacsweers.metrox.viewmodel.metroViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dertefter.settings_ai.presentation.SettingsAiScreen

@Composable
fun SettingsAiRoute(
    viewModel: SettingsAiViewModel = metroViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SettingsAiScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
        }
    )
}
