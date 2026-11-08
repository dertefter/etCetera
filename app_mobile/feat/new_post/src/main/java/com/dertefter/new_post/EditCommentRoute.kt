package com.dertefter.new_post

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dertefter.new_post.presentation.NewPostScreen
import com.dertefter.new_post.presentation.ScreenMode
import dev.zacsweers.metrox.viewmodel.metroViewModel

@Composable
fun EditCommentRoute(
    postId: String,
    commentId: String,
    viewModel: EditCommentViewModel = metroViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(commentId) {
        viewModel.initWith(postId, commentId)
    }

    NewPostScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
        },
        screenMode = ScreenMode.EDIT_COMMENT
    )
}
