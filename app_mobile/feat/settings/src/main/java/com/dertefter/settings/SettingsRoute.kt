package com.dertefter.settings

import androidx.compose.runtime.Composable
import dev.zacsweers.metrox.viewmodel.metroViewModel
import com.dertefter.settings.presentation.SettingsScreen

@Composable
fun SettingsRoute(
    viewModel: SettingsViewModel = metroViewModel(),
) {
    SettingsScreen(
        onEvent = viewModel::onEvent
    )
}
