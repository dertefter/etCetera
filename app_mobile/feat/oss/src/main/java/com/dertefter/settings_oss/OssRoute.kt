package com.dertefter.settings_oss

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.zacsweers.metrox.viewmodel.metroViewModel
import com.dertefter.settings_oss.presentation.OssScreen

@Composable
fun OssRoute(
    viewModel: OssViewModel = metroViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    OssScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent
    )
}
