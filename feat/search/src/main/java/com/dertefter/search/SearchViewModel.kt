package com.dertefter.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dertefter.data.repository.SearchRepository
import com.dertefter.navigation.Navigator
import com.dertefter.navigation.Routes
import com.dertefter.search.presentation.Event
import com.dertefter.search.presentation.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    private val navigator: Navigator
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        viewModelScope.launch {
            searchRepository.getTrendingHashtags().collect { hashtags ->
                if (hashtags != null && _uiState.value.query.isEmpty()) {
                    _uiState.update { it.copy(hashtags = hashtags, users = emptyList()) }
                }
            }
        }
        viewModelScope.launch {
            searchRepository.updateTrendingHashtags()
        }
    }

    fun onEvent(event: Event) {
        when (event) {
            is Event.OnSearchQueryChanged -> {
                _uiState.update { it.copy(query = event.q) }
                getSearchResults(event.q)
            }
            is Event.OnNavigateBack -> { navigator.navigateUp() }
            is Event.OnOpenUser -> { navigator.navigate(Routes.User(event.userId)) }
            is Event.OnOpenHashtag -> { navigator.navigate(Routes.HashtagFeed(event.name)) }
        }
    }

    private fun getSearchResults(q: String) {
        searchJob?.cancel()
        if (q.isBlank()) {
            return
        }
        searchJob = viewModelScope.launch {
            delay(300.milliseconds)
            _uiState.update { it.copy(isLoading = true) }
            searchRepository.getSearchResults(q)
                .onSuccess { data ->
                    _uiState.update {
                        it.copy(
                            users = data.users,
                            hashtags = data.hashtags,
                            isLoading = false,
                            isError = false
                        )
                    }
                }
                .onFailure {
                    _uiState.update { it.copy(isLoading = false, isError = true) }
                }
        }
    }
}
