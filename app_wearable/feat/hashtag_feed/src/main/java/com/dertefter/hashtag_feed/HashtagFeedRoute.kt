package com.dertefter.hashtag_feed

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dertefter.hashtag_feed.presentation.Event
import com.dertefter.hashtag_feed.presentation.HashtagFeedScreen
import com.dertefter.hashtag_feed.presentation.UiState

@Composable
fun HashtagFeedRoute(
    hashtagName: String? = null,
    viewModel: HashtagFeedViewModel = hiltViewModel(),
) {

    val hashtagUiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(hashtagName) {
        viewModel.initHashtagName(hashtagName)
    }

    LaunchedEffect(Unit) {
        viewModel.onEvent(Event.OnRefresh)
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
