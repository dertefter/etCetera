package com.dertefter.new_post

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dertefter.new_post.presentation.NewPostScreen

@Composable
fun NewPostRoute(
    wallRecipientId: String?,
    postIdForRepost: String?,
    viewModel: NewPostViewModel = hiltViewModel(),
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(wallRecipientId, postIdForRepost) {
        viewModel.initWith(wallRecipientId, postIdForRepost)
    }

    NewPostScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
        },

    )


}
