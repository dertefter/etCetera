package com.dertefter.settings_account

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.dertefter.settings_account.presentation.SettingsAccountScreen

@Composable
fun SettingsAccountRoute(
    viewModel: SettingsAccountViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    SettingsAccountScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent
    )

}
