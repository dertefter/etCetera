package com.dertefter.post.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dertefter.data.repository.FeedRepository
import com.dertefter.data.repository.MeRepository
import com.dertefter.navigation.Navigator
import com.dertefter.navigation.Routes
import com.dertefter.post.presentation.mapper.toNavigationModel
import com.dertefter.post.presentation.mapper.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val feedRepository: FeedRepository,
    private val meRepository: MeRepository,
    private val navigator: Navigator,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val meUserId: StateFlow<String?> = meRepository.meDto
        .map { it?.id }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    private val postId: String = checkNotNull(savedStateHandle["postId"])

    private val _isLoading = MutableStateFlow(false)

    val uiState: StateFlow<UiState> = combine(
        feedRepository.getPost(postId),
        _isLoading
    ) { post, isLoading ->
        UiState(
            post = post.toUiModel(),
            isLoading = isLoading
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Companion.WhileSubscribed(5000),
        UiState(isLoading = true)
    )

    init {
        viewModelScope.launch {
            _isLoading.value = true
            feedRepository.updatePost(postId)
            _isLoading.value = false
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

            is Event.OnRefresh -> {
                viewModelScope.launch {
                    _isLoading.value = true
                    feedRepository.updatePost(postId)
                    _isLoading.value = false
                }
            }

            is Event.OnLike -> {
                viewModelScope.launch {
                    feedRepository.likePost(postId)
                }
            }

            is Event.OnNavigateBack -> {
                navigator.navigateUp()
            }

            is Event.OnUnlike -> {
                viewModelScope.launch {
                    feedRepository.unlikePost(postId)
                }
            }

            is Event.OnVote -> {
                viewModelScope.launch {
                    feedRepository.votePoll(postId, event.optionIds)
                }
            }
        }
    }
}