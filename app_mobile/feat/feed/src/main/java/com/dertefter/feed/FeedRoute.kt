package com.dertefter.feed

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dertefter.feed.presentation.Event
import com.dertefter.feed.presentation.FeedScreen
import com.dertefter.feed.presentation.FeedTab

@Composable
fun FeedRoute(
    viewModel: FeedViewModel = hiltViewModel(),
) {

    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()

    val popularUiState by viewModel.uiStates[FeedTab.POPULAR]!!.collectAsStateWithLifecycle()
    val clanUiState by viewModel.uiStates[FeedTab.CLAN]!!.collectAsStateWithLifecycle()
    val followingUiState by viewModel.uiStates[FeedTab.FOLLOWING]!!.collectAsStateWithLifecycle()

    val uiStates = mapOf(
        FeedTab.POPULAR to popularUiState,
        FeedTab.CLAN to clanUiState,
        FeedTab.FOLLOWING to followingUiState
    )

    val paginators = viewModel.tabs.associateWith { viewModel.getPaginator(it) }

    FeedScreen(
        onEvent = viewModel::onEvent,
        selectedTab = selectedTab,
        uiStates = uiStates,
        paginators = paginators
    )


}
