package com.dertefter.settings_account

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.zacsweers.metrox.viewmodel.metroViewModel
import com.dertefter.settings_account.presentation.SettingsAccountScreen

@Composable
fun SettingsAccountRoute(
    viewModel: SettingsAccountViewModel = metroViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    SettingsAccountScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent
    )

}
