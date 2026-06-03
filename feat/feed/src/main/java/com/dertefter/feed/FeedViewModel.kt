package com.dertefter.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dertefter.data.dto.feed.PostDto
import com.dertefter.data.repository.FeedRepository
import com.dertefter.data.repository.MeRepository
import com.dertefter.data.repository.SearchRepository
import com.dertefter.feed.presentation.Event
import com.dertefter.feed.presentation.FeedTab
import com.dertefter.feed.presentation.TopBarUiState
import com.dertefter.navigation.Navigator
import com.dertefter.navigation.Routes
import com.jamal_aliev.paginator.MutableCursorPaginator
import com.jamal_aliev.paginator.extension.distinctBy
import com.jamal_aliev.paginator.extension.prefetchController
import com.jamal_aliev.paginator.extension.uiState
import com.jamal_aliev.paginator.extension.warmUpFromPersistent
import com.jamal_aliev.paginator.page.PaginatorUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val feedRepository: FeedRepository,
    private val searchRepository: SearchRepository,
    private val meRepository: MeRepository,
    private val navigator: Navigator

) : ViewModel() {

    val tabs = FeedTab.entries

    private val _selectedTab = MutableStateFlow(FeedTab.POPULAR)
    val selectedTab: StateFlow<FeedTab> = _selectedTab.asStateFlow()

    private val paginators = tabs.associateWith { tab ->
        feedRepository.getFeedPaginator(tab.value)
    }

    private val _trendingHashtags = searchRepository.getTrendingHashtags()

    private val _emojiAvatar = meRepository.meDto.map { it?.avatar }

    fun getPaginator(tab: FeedTab) = paginators[tab]!!


    val topBarUiState: StateFlow<TopBarUiState> = combine(
        _trendingHashtags,
        _emojiAvatar
    ) { hashtags, avatar ->
        TopBarUiState(
            trendingHashtags = hashtags,
            avatarEmoji = avatar,
            notificationsCount = null
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TopBarUiState()
    )

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

    private fun updateTrendingHashtags(){
        viewModelScope.launch {
            searchRepository.updateTrendingHashtags()
        }
    }

    private fun updateMe(){
        viewModelScope.launch {
            meRepository.fetchMe()
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

            is Event.OnOpenAttachmentsViewer -> {
                navigator.navigate(Routes.ImageViewer(
                    event.urls, event.position
                ))
            }

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
                updateTrendingHashtags()
                updateMe()
                viewModelScope.launch {
                    getPaginator(event.tab).restart()
                }
            }
            Event.OnOpenNotifications -> {
                navigator.navigate(Routes.Notifications)
            }
            is Event.OnNavigateToComments -> {
                navigator.openAsBottomSheet(Routes.Comments(event.postId))
            }

            is Event.OnOpenUser -> {
                navigator.navigate(
                    Routes.User(event.userId)
                )
            }

            is Event.OnOpenPost -> {
                navigator.navigate(
                    Routes.Post(event.postId)
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
