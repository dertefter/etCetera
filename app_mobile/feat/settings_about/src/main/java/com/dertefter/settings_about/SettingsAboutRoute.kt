package com.dertefter.settings_about

import androidx.compose.runtime.Composable
import dev.zacsweers.metrox.viewmodel.metroViewModel
import com.dertefter.settings_about.presentation.SettingsAboutScreen

@Composable
fun SettingsAboutRoute(
    viewModel: SettingsAboutViewModel = metroViewModel(),
) {

    SettingsAboutScreen(
        onEvent = viewModel::onEvent
    )

}
