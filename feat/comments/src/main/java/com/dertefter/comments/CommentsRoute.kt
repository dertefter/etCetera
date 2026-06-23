package com.dertefter.comments

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dertefter.comments.presentation.CommentSort
import com.dertefter.comments.presentation.Event
import com.dertefter.comments.presentation.CommentsScreen

@Composable
fun CommentsRoute(
    postId: String,
    viewModel: CommentsViewModel = hiltViewModel(),
) {

    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val meUserId by viewModel.meUserId.collectAsStateWithLifecycle()

    val popularUiState by viewModel.getUiState(postId, CommentSort.POPULAR).collectAsStateWithLifecycle()
    val oldestUiState by viewModel.getUiState(postId, CommentSort.OLDEST).collectAsStateWithLifecycle()
    val newestUiState by viewModel.getUiState(postId, CommentSort.NEWEST).collectAsStateWithLifecycle()

    val uiStates = mapOf(
        CommentSort.POPULAR to popularUiState,
        CommentSort.OLDEST to oldestUiState,
        CommentSort.NEWEST to newestUiState
    )

    val paginators = viewModel.sorts.associateWith { sort ->
        viewModel.getPaginator(postId, sort)
    }

    LaunchedEffect(postId, selectedTab) {
        viewModel.onEvent(Event.OnRefresh(selectedTab, postId))
    }

    CommentsScreen(
        meUserId = meUserId,
        postId = postId,
        onEvent = viewModel::onEvent,
        selectedTab = selectedTab,
        uiStates = uiStates,
        paginators = paginators
    )


}
