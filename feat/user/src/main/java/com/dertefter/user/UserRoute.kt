package com.dertefter.user

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dertefter.user.presentation.UserScreen

@Composable
fun UserRoute(
    userId: String? = null,
    viewModel: UserViewModel = hiltViewModel(),
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val paginators by viewModel.paginators.collectAsStateWithLifecycle()

    val showBack: Boolean = userId != null

    LaunchedEffect(Unit) {
        viewModel.initWithUserId(userId)
    }

    UserScreen(
        uiState = uiState,
        showBack = showBack,
        paginators = paginators,
        onEvent = { event ->
            viewModel.onEvent(event)
        },
    )


}
