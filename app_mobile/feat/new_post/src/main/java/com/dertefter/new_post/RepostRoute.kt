package com.dertefter.new_post

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import dev.zacsweers.metrox.viewmodel.metroViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dertefter.new_post.presentation.NewPostScreen
import com.dertefter.new_post.presentation.ScreenMode

@Composable
fun RepostRoute(
    postIdForRepost: String,
    wallRecipientId: String?,
    viewModel: RepostViewModel = metroViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(postIdForRepost, wallRecipientId) {
        viewModel.initWith(postIdForRepost, wallRecipientId)
    }

    NewPostScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
        },
        screenMode = ScreenMode.REPOST
    )
}
