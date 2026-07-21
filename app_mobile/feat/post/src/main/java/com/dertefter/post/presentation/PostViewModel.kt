package com.dertefter.post.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dertefter.data.repository.MeRepository
import com.dertefter.data.repository.PostRepository
import com.dertefter.navigation.Navigator
import com.dertefter.navigation.Routes
import com.dertefter.post.presentation.mapper.toNavigationModel
import com.dertefter.post.presentation.mapper.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val postRepository: PostRepository,
    meRepository: MeRepository,
    private val navigator: Navigator
) : ViewModel() {

    val meUserId: StateFlow<String?> = meRepository.meDto
        .map { it?.id }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    private val _postId = MutableStateFlow<String?>(null)

    private val _isLoading = MutableStateFlow(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<UiState> = _postId.filterNotNull().flatMapLatest { id ->
        combine(
            postRepository.getPost(id),
            _isLoading
        ) { post, isLoading ->
            UiState(
                post = post?.toUiModel(),
                isLoading = isLoading
            )
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        UiState()
    )

    fun initWithPostId(id: String) {
        if (_postId.value != null) return
        _postId.value = id
        viewModelScope.launch {
            postRepository.updatePost(id)
        }
    }

    fun onEvent(event: Event) {
        when (event) {

            is Event.OnOpenAttachmentsViewer -> {
                navigator.navigate(
                    Routes.AttachmentsViewer(
                        attachments = event.attachments.map {it.toNavigationModel()},
                        viewPosition = event.position)
                )
            }

            is Event.OnOpenUser -> {
                navigator.navigate(Routes.User(event.userId))
            }

            is Event.OnOpenPost -> {
                navigator.navigate(Routes.User(event.postId))
            }

            is Event.OnOpenHashtag -> {
                navigator.navigate(Routes.HashtagFeed(event.name))
            }

            is Event.OnDeletePost -> {
                viewModelScope.launch {
                    postRepository.deletePost(event.postId).onSuccess {
                        navigator.navigateUp()
                    }
                }

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

            is Event.OnRepost -> {
                navigator.openAsBottomSheet(
                    Routes.NewPost(postIdForRepost = event.postId)
                )
            }


            is Event.OnRefresh -> {
                viewModelScope.launch {
                    val id = _postId.value ?: return@launch
                    _isLoading.value = true
                    postRepository.updatePost(id)
                    _isLoading.value = false
                }
            }

            is Event.OnLike -> {
                viewModelScope.launch {
                    val id = _postId.value ?: return@launch
                    postRepository.likePost(id)
                }
            }

            is Event.OnNavigateBack -> {
                navigator.navigateUp()
            }

            is Event.OnUnlike -> {
                viewModelScope.launch {
                    val id = _postId.value ?: return@launch
                    postRepository.unlikePost(id)
                }
            }

            is Event.OnVote -> {
                viewModelScope.launch {
                    val id = _postId.value ?: return@launch
                    postRepository.votePoll(id, event.optionIds)
                }
            }
        }
    }
}