package com.dertefter.settings_security

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.dertefter.settings_security.presentation.SettingsSecurityScreen

@Composable
fun SettingsSecurityRoute(
    viewModel: SettingsSecurityViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    SettingsSecurityScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent
    )

}
