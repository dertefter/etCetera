package com.dertefter.settings_theme

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.zacsweers.metrox.viewmodel.metroViewModel
import com.dertefter.settings_theme.presentation.SettingsThemeScreen

@Composable
fun SettingsThemeRoute(
    viewModel: SettingsThemeViewModel = metroViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    AnimatedVisibility(
        visible = uiState != null
    ) {
        uiState?.let { uiState ->
            SettingsThemeScreen(
                uiState = uiState,
                onEvent = viewModel::onEvent
            )
        }

    }

}
