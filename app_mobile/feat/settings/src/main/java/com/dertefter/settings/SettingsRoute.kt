package com.dertefter.settings

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.dertefter.settings.presentation.SettingsScreen

@Composable
fun SettingsRoute(
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    SettingsScreen(
        onEvent = viewModel::onEvent
    )
}
