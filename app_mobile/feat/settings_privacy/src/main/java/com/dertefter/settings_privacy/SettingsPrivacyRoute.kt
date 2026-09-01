package com.dertefter.settings_privacy

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.dertefter.settings_privacy.presentation.SettingsPrivacyScreen

@Composable
fun SettingsPrivacyRoute(
    viewModel: SettingsPrivacyViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    SettingsPrivacyScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent
    )

}
