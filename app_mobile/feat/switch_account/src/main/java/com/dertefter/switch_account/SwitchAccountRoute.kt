package com.dertefter.switch_account

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import dev.zacsweers.metrox.viewmodel.metroViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dertefter.switch_account.presentation.SwitchAccountScreen

@Composable
fun SwitchAccountRoute(
    viewModel: SwitchAccountViewModel = metroViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SwitchAccountScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
        }
    )
}
