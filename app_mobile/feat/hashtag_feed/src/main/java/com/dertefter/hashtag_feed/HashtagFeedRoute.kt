package com.dertefter.hashtag_feed

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dertefter.hashtag_feed.presentation.HashtagFeedScreen
import com.dertefter.hashtag_feed.presentation.UiState

@Composable
fun HashtagFeedRoute(
    hashtagName: String? = null,
    viewModel: HashtagFeedViewModel = hiltViewModel(),
) {

    val hashtagUiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.initHashtagName(hashtagName)
    }

    HashtagFeedScreen(
        onEvent = viewModel::onEvent,
        uiState = UiState(
            hashtag = hashtagName,
            uiState = hashtagUiState
        ),
        paginator = viewModel.getPaginator()
    )


}
