package com.dertefter.edbi_users

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dertefter.edbi_users.presentation.EdbiUsersScreen
import dev.zacsweers.metrox.viewmodel.metroViewModel

@Composable
fun EdbiUsers(
    clan: String,
    viewModel: EdbiUsersViewModel = metroViewModel(),
) {
    remember(clan) {
        viewModel.initWithClan(clan)
        true
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val paginator by viewModel.paginator.collectAsStateWithLifecycle()

    EdbiUsersScreen(
        onEvent = viewModel::onEvent,
        uiState = uiState,
        paginator = paginator
    )
}
