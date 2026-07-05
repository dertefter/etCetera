package com.dertefter.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dertefter.search.presentation.Event
import com.dertefter.search.presentation.SearchScreen

@Composable
fun SearchRoute(
    viewModel: SearchViewModel = hiltViewModel(),
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
