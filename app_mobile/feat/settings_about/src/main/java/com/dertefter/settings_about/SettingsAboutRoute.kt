package com.dertefter.settings_about

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.dertefter.settings_about.presentation.SettingsAboutScreen

@Composable
fun SettingsAboutRoute(
    viewModel: SettingsAboutViewModel = hiltViewModel(),
) {

    SettingsAboutScreen(
        onEvent = viewModel::onEvent
    )

}
