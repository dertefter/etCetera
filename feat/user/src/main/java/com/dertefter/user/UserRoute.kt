package com.dertefter.user

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dertefter.user.presentation.Event
import com.dertefter.user.presentation.UserScreen

@Composable
fun UserRoute(
    userId: String? = null,
    viewModel: UserViewModel = hiltViewModel(),
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val paginators by viewModel.paginators.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.initWithUserId(userId)
    }

    UserScreen(
        uiState = uiState,
        paginators = paginators,
        onEvent = viewModel::onEvent
    )


}
