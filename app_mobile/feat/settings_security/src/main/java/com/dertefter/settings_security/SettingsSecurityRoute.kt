package com.dertefter.settings_security

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.zacsweers.metrox.viewmodel.metroViewModel
import com.dertefter.settings_security.presentation.SettingsSecurityScreen

@Composable
fun SettingsSecurityRoute(
    viewModel: SettingsSecurityViewModel = metroViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    SettingsSecurityScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent
    )

}
