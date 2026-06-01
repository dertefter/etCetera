package com.dertefter.crash_reports

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dertefter.crash_reports.presentation.CrashReportsScreen
import com.dertefter.crash_reports.presentation.Event
import java.io.File

@Composable
fun CrashReportsRoute(
    viewModel: CrashReportsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.onEvent(Event.OnRefresh)
    }

    CrashReportsScreen(
        onEvent = { event ->
            if (event is Event.OnShareReport) {
                val file = File(event.path)
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(Intent.createChooser(intent, "Share Crash Report"))
            } else {
                viewModel.onEvent(event)
            }
        },
        uiState = uiState
    )
}
