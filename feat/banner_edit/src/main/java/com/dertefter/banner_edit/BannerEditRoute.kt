package com.dertefter.banner_edit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dertefter.banner_edit.presentation.BannerEditScreen

@Composable
fun BannerEditRoute(
    viewModel: BannerEditViewModel = hiltViewModel(),
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BannerEditScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
        },

    )


}
