package com.dertefter.settings_privacy

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.zacsweers.metrox.viewmodel.metroViewModel
import com.dertefter.settings_privacy.presentation.SettingsPrivacyScreen

@Composable
fun SettingsPrivacyRoute(
    viewModel: SettingsPrivacyViewModel = metroViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    SettingsPrivacyScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent
    )

}
