package com.dertefter.post

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import dev.zacsweers.metrox.viewmodel.metroViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dertefter.post.presentation.PostScreen
import com.dertefter.post.presentation.PostViewModel

@Composable
fun PostRoute(
    postId: String,
    viewModel: PostViewModel = metroViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val meUserId by viewModel.meUserId.collectAsStateWithLifecycle()

    LaunchedEffect(postId) {
        viewModel.initWithPostId(postId)
    }

    PostScreen(
        onEvent = viewModel::onEvent,
        uiState = uiState,
        meUserId = meUserId
    )
}
