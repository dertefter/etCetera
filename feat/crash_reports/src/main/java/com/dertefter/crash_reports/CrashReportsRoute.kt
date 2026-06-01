package com.dertefter.crash_reports

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dertefter.crash_reports.presentation.CrashReportsScreen
import com.dertefter.crash_reports.presentation.Event

@Composable
fun CrashReportsRoute(
    viewModel: CrashReportsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onEvent(Event.OnRefresh)
    }

    CrashReportsScreen(
        onEvent = viewModel::onEvent,
        uiState = uiState
    )
}
