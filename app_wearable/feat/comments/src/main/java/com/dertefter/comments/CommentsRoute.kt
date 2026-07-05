package com.dertefter.comments

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dertefter.comments.presentation.Event
import com.dertefter.comments.presentation.CommentsScreen

@Composable
fun CommentsRoute(
    postId: String,
    viewModel: CommentsViewModel = hiltViewModel(),
) {
    val meUserId by viewModel.meUserId.collectAsStateWithLifecycle()

    val uiState by viewModel.getUiState(postId).collectAsStateWithLifecycle()

    val paginator = viewModel.getPaginator(postId)

    LaunchedEffect(postId) {
        viewModel.onEvent(Event.OnRefresh(postId))
    }

    CommentsScreen(
        postId = postId,
        meUserId = meUserId,
        onEvent = viewModel::onEvent,
        uiState = uiState,
        paginator = paginator
    )
}
