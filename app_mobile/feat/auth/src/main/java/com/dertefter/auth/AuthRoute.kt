package com.dertefter.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dertefter.auth.presentation.AuthScreen
import dev.zacsweers.metrox.viewmodel.metroViewModel

@Composable
fun AuthRoute(
    viewModel: AuthViewModel = metroViewModel(),
) {

    val uiState by viewModel.state.collectAsStateWithLifecycle()

    AuthScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
        },
    )


}
