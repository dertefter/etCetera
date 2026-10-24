package com.dertefter.pin_changer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.zacsweers.metrox.viewmodel.metroViewModel
import com.dertefter.pin_changer.presentation.PinChangerScreen

@Composable
fun PinChangerRoute(
    viewModel: PinChangerViewModel = metroViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    PinChangerScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent
    )

}
