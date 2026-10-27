package com.dertefter.hashtag_feed

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
import com.jamal_aliev.paginator.cursor.extension.uiState
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
@ViewModelKey
@ContributesIntoMap(AppScope::class)
@Inject
class HashtagFeedViewModel(
    private val feedRepository: FeedRepository,
    private val postRepository: PostRepository,
    private val navigator: Navigator
) : ViewModel() {

    private val _hashtagName = MutableStateFlow<String?>(null)

    private var paginator: MutableCursorPaginator<String, PostDto>? = null
    private var paginatorHashtag: String? = null

    val uiState: StateFlow<PaginatorUiState<PostDto>> = _hashtagName
        .filterNotNull()
        .flatMapLatest { name ->
            getOrCreatePaginator(name).uiState
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PaginatorUiState.Idle
        )

    /**
     * Reuses the paginator of the current hashtag so a re-subscription (for example after
     * [SharingStarted.WhileSubscribed] stops the flow) does not throw away the loaded pages.
     * Switching to another hashtag releases the previous paginator before a new one is built.
     */
    private fun getOrCreatePaginator(name: String): MutableCursorPaginator<String, PostDto> {
        paginator?.takeIf { paginatorHashtag == name }?.let { return it }
        paginator?.release()
        return feedRepository.getHashtagPaginator(name).also {
            paginator = it
            paginatorHashtag = name
            setupPaginator(it)
        }
    }

    fun initHashtagName(hashtagName: String?) {
        if (_hashtagName.value == hashtagName) return
        _hashtagName.value = hashtagName
    }

    private fun setupPaginator(paginator: MutableCursorPaginator<String, PostDto>) {
        viewModelScope.launch {
            // Prefetch is owned by rememberPaginated() on the UI side: it creates and binds
            // its own controller, so one built here would never be attached to the list.
            paginator.restart()
        }
    }

    fun getPaginator() = paginator

    fun onEvent(event: Event) {
        when (event) {

            is Event.OnOpenAttachmentsViewer -> {
                navigator.navigate(
                    Routes.AttachmentsViewer(
                        attachments = event.attachments.map { it.toNavigationModel() },
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
                    Routes.Repost(postIdForRepost = event.postId)
                )
            }

            is Event.OnEditPost -> {
                navigator.openAsBottomSheet(
                    Routes.EditPost(postId = event.postId)
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
                        postRepository.updatePostStats(event.ids)
                    }
                }
            }

            Event.OnLoadMore -> {
                // Handled by the prefetch controller owned by rememberPaginated() on the list
            }

            is Event.OnRefresh -> {
                viewModelScope.launch {
                    paginator?.restart(silentlyLoading = true)
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

            is Event.OnReport -> {
                navigator.openAsBottomSheet(
                    Routes.Report(targetType = event.targetType, targetId = event.targetId)
                )
            }

        }
    }

    override fun onCleared() {
        paginator?.release()
        paginator = null
        paginatorHashtag = null
    }
}
