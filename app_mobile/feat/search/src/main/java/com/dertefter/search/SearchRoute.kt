package com.dertefter.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import dev.zacsweers.metrox.viewmodel.metroViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dertefter.search.presentation.Event
import com.dertefter.search.presentation.SearchScreen

@Composable
fun SearchRoute(
    viewModel: SearchViewModel = metroViewModel(),
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onEvent(Event.OnSearchQueryChanged(""))
    }

    SearchScreen(
        onEvent = viewModel::onEvent,
        uiState = uiState
    )
}
