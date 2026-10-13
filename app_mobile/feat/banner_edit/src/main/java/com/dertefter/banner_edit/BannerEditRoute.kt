package com.dertefter.banner_edit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import dev.zacsweers.metrox.viewmodel.metroViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dertefter.banner_edit.presentation.BannerEditScreen

@Composable
fun BannerEditRoute(
    viewModel: BannerEditViewModel = metroViewModel(),
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BannerEditScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
        },

    )


}
