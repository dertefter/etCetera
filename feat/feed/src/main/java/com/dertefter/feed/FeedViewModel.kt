package com.dertefter.feed

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dertefter.data.dto.feed.PostDto
import com.dertefter.data.repository.FeedRepository
import com.dertefter.feed.presentation.Event
import com.dertefter.feed.presentation.FeedTab
import com.dertefter.navigation.Navigator
import com.dertefter.navigation.Routes
import com.dertefter.navigation.Routes.*
import com.jamal_aliev.paginator.MutableCursorPaginator
import com.jamal_aliev.paginator.page.PaginatorUiState
import com.jamal_aliev.paginator.extension.uiState
import com.jamal_aliev.paginator.extension.warmUpFromPersistent
import com.jamal_aliev.paginator.extension.distinctBy
import com.jamal_aliev.paginator.extension.prefetchController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val feedRepository: FeedRepository,
    private val navigator: Navigator
) : ViewModel() {

    val tabs = FeedTab.entries

    private val _selectedTab = MutableStateFlow(FeedTab.POPULAR)
    val selectedTab: StateFlow<FeedTab> = _selectedTab.asStateFlow()

    private val paginators = tabs.associateWith { tab ->
        feedRepository.getFeedPaginator(tab.value)
    }

    fun getPaginator(tab: FeedTab) = paginators[tab]!!

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiStates: Map<FeedTab, StateFlow<PaginatorUiState<PostDto>>> = paginators.mapValues { (_, paginator) ->
        paginator.uiState.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PaginatorUiState.Idle
        )
    }

    init {
        paginators.values.forEach {
            setupPaginator(it)
        }
    }

    private fun setupPaginator(paginator: MutableCursorPaginator<PostDto>) {
        viewModelScope.launch {
            paginator.distinctBy { it.id }
            paginator.prefetchController(
                scope = viewModelScope,
                prefetchDistance = 3
            )
            paginator.warmUpFromPersistent()
            paginator.restart(silentlyLoading = true)
        }
    }

    fun onEvent(event: Event) {
        when (event) {

            is Event.OnLike -> {
                viewModelScope.launch {
                    feedRepository.likePost(event.postId)
                }
            }

            is Event.OnUnlike -> {
                viewModelScope.launch {
                    feedRepository.unlikePost(event.postId)
                }
            }

            is Event.OnVote -> {
                viewModelScope.launch {
                    feedRepository.votePoll(event.postId, event.optionIds)
                }
            }

            is Event.OnUpdateStats -> {
                viewModelScope.launch {
                    if (event.ids.isNotEmpty()){
                        feedRepository.updatePostStats(event.ids)
                    }
                }
            }

            is Event.OnTabSelected -> {
                if (_selectedTab.value != event.tab) {
                    _selectedTab.value = event.tab
                }
            }
            Event.OnLoadMore -> {
                // Handled by prefetchController
            }
            is Event.OnRefresh -> {
                viewModelScope.launch {
                    getPaginator(event.tab).restart()
                }
            }
            Event.OnNavigateBack -> { /* Handle */ }
            is Event.OnNavigateToComments -> {
                navigator.openAsBottomSheet(Comments(event.postId))
            }

            is Event.OnOpenUser -> {
                navigator.navigate(
                    Routes.User(event.userId)
                )
            }

            else -> {
                navigator.openAsBottomSheet(Routes.NewPost(null))
            }
        }
    }

    override fun onCleared() {
        paginators.values.forEach { it.release() }
        super.onCleared()
    }
}
