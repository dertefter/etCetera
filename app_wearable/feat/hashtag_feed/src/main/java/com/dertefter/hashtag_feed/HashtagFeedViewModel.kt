package com.dertefter.hashtag_feed

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dertefter.data.dto.feed.PostDto
import com.dertefter.data.repository.FeedRepository
import com.dertefter.data.repository.PostRepository
import com.dertefter.hashtag_feed.presentation.Event
import com.dertefter.hashtag_feed.presentation.mapper.toNavigationModel
import com.dertefter.navigation.Navigator
import com.dertefter.navigation.Routes
import com.dertefter.navigation.Routes.Comments
import com.jamal_aliev.paginator.core.page.PaginatorUiState
import com.jamal_aliev.paginator.cursor.MutableCursorPaginator
import com.jamal_aliev.paginator.cursor.bookmark.CursorBookmark
import com.jamal_aliev.paginator.cursor.extension.distinctBy
import com.jamal_aliev.paginator.cursor.extension.prefetchController
import com.jamal_aliev.paginator.cursor.extension.refreshAll
import com.jamal_aliev.paginator.cursor.extension.uiState
import com.jamal_aliev.paginator.cursor.extension.warmUpFromPersistent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HashtagFeedViewModel @Inject constructor(
    private val feedRepository: FeedRepository,
    private val postRepository: PostRepository,
    private val navigator: Navigator
) : ViewModel() {

    private val _hashtagName = MutableStateFlow<String?>(null)

    private val _paginators = MutableStateFlow<Map<String, MutableCursorPaginator<String, PostDto>>>(emptyMap())

    fun getPaginator() = _hashtagName.value?.let { _paginators.value[it] }

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<PaginatorUiState<PostDto>> = combine(
        _hashtagName,
        _paginators
    ) { name, paginatorsMap ->
        if (name == null) flowOf(PaginatorUiState.Idle)
        else paginatorsMap[name]?.uiState ?: flowOf(PaginatorUiState.Idle)
    }.flatMapLatest { it }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PaginatorUiState.Idle
        )

    fun initHashtagName(hashtagName: String?) {
        if (_hashtagName.value == hashtagName) return
        _hashtagName.value = hashtagName

        _paginators.value.values.forEach { it.release() }
        if (hashtagName != null) {
            val p = feedRepository.getHashtagPaginator(hashtagName)
            _paginators.value = mapOf(hashtagName to p)
            setupPaginator(p)
        } else {
            _paginators.value = emptyMap()
        }
    }

    private fun setupPaginator(paginator: MutableCursorPaginator<String, PostDto>) {
        viewModelScope.launch {
            paginator.distinctBy { it.id }
            paginator.prefetchController(
                scope = viewModelScope, prefetchDistance = 3
            )
            val inserted = paginator.warmUpFromPersistent()
            if (inserted > 0) {
                paginator.jump(CursorBookmark(prev = null, self = "initial", next = null))
                paginator.refreshAll(loadingSilently = true)
            } else {
                paginator.restart(silentlyLoading = true)
            }
        }
    }

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

            is Event.OnPin -> {
                viewModelScope.launch {
                    postRepository.pinPost(event.postId)
                }
            }

            is Event.OnUnpin -> {
                viewModelScope.launch {
                    postRepository.unpinPost(event.postId)
                }
            }

            is Event.OnVote -> {
                viewModelScope.launch {
                    postRepository.votePoll(event.postId, event.optionIds)
                }
            }

            is Event.OnRepost -> {
                navigator.openAsBottomSheet(
                    Routes.NewPost(postIdForRepost = event.postId)
                )
            }

            is Event.OnOpenHashtag -> {
                navigator.navigate(Routes.HashtagFeed(event.name))
            }

            is Event.OnDeletePost -> {
                viewModelScope.launch {
                    postRepository.deletePost(event.postId)
                }
            }

            is Event.OnOpenPost -> {
                navigator.navigate(Routes.Post(event.postId))
            }

            Event.OnNavigateBack -> {
                navigator.navigateUp()
            }

            is Event.OnLike -> {
                viewModelScope.launch {
                    postRepository.likePost(event.postId)
                }
            }

            is Event.OnUnlike -> {
                viewModelScope.launch {
                    postRepository.unlikePost(event.postId)
                }
            }

            is Event.OnUpdateStats -> {
                viewModelScope.launch {
                    if (event.ids.isNotEmpty()) {
                        postRepository.updatePostStats(event.ids).onFailure {
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
                    getPaginator()?.restart(silentlyLoading = true)
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
        _paginators.value.values.forEach { it.release() }
    }
}
