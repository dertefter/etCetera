package com.dertefter.new_comment

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dertefter.new_comment.presentation.NewCommentScreen

@Composable
fun NewCommentRoute(
    postId: String,
    viewModel: NewCommentViewModel = hiltViewModel(),
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(postId) {
        viewModel.initCommentForPost(postId)
    }

    NewCommentScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
        },

    )
}
@Composable
fun NewCommentReplyRoute(
    postId: String,
    commentId: String,
    replyToUserId: String,
    viewModel: NewCommentViewModel = hiltViewModel(),
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(commentId, replyToUserId) {
        viewModel.initCommentForReply(postId, commentId, replyToUserId)
    }

    NewCommentScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
        },
        )
}

