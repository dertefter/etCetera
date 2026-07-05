package com.dertefter.post

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dertefter.post.presentation.PostScreen
import com.dertefter.post.presentation.PostViewModel

@Composable
fun PostRoute(
    postId: String,
    viewModel: PostViewModel = hiltViewModel(),
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
