package com.dertefter.hashtag_feed

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dertefter.data.repository.FeedRepository
import com.dertefter.navigation.Navigator
import com.dertefter.navigation.Routes
import com.dertefter.navigation.Routes.Comments
import com.dertefter.data.dto.feed.PostDto
import com.dertefter.hashtag_feed.presentation.Event
import com.dertefter.hashtag_feed.presentation.mapper.toNavigationModel
import com.jamal_aliev.paginator.core.page.PaginatorUiState
import com.jamal_aliev.paginator.cursor.MutableCursorPaginator
import com.jamal_aliev.paginator.cursor.extension.distinctBy
import com.jamal_aliev.paginator.cursor.extension.prefetchController
import com.jamal_aliev.paginator.cursor.extension.uiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HashtagFeedViewModel @Inject constructor(
    private val feedRepository: FeedRepository,
    private val navigator: Navigator
) : ViewModel() {

    private val _hashtagName = MutableStateFlow<String?>(null)

    private var paginator: MutableCursorPaginator<String, PostDto>? = null

    val uiState: StateFlow<PaginatorUiState<PostDto>> = _hashtagName
        .filterNotNull()
        .flatMapLatest { name ->
            val p = feedRepository.getHashtagPaginator(name)
            paginator = p
            setupPaginator(p)
            p.uiState
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PaginatorUiState.Idle
        )

    fun initHashtagName(hashtagName: String?) {
        if (_hashtagName.value == hashtagName) return
        _hashtagName.value = hashtagName
    }

    private fun setupPaginator(paginator: MutableCursorPaginator<String, PostDto>) {
        viewModelScope.launch {
            paginator.distinctBy { it.id }
            paginator.prefetchController(
                scope = viewModelScope, prefetchDistance = 3
            )
            paginator.restart()
        }
    }

    fun getPaginator() = paginator

    fun onEvent(event: Event) {
        when (event) {

            is Event.OnOpenAttachmentsViewer -> {
                navigator.navigate(
                    Routes.AttachmentsViewer(
                        attachments = event.attachments.map {it.toNavigationModel()},
                        viewPosition = event.position
                    )
                )
            }

            is Event.OnOpenPost -> {
                navigator.navigate(Routes.Post(event.postId))
            }

            Event.OnNavigateBack -> {
                navigator.navigateUp()
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

            is Event.OnUpdateStats -> {
                viewModelScope.launch {
                    if (event.ids.isNotEmpty()) {
                        feedRepository.updatePostStats(event.ids).onFailure {
                            Log.e("HashtagFeedViewModel", it.stackTraceToString())
                        }
                    }
                }
            }

            Event.OnLoadMore -> {
                // Handled by prefetchController
            }

            is Event.OnRefresh -> {
                viewModelScope.launch {
                    paginator?.restart()
                }
            }

            is Event.OnNavigateToComments -> {
                navigator.openAsBottomSheet(Comments(event.postId))
            }


            is Event.OnOpenUser -> {
                navigator.navigate(
                    Routes.User(event.userId)
                )
            }


        }
    }

    override fun onCleared() {
        paginator?.release()
    }
}
