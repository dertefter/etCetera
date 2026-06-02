package com.dertefter.comments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dertefter.comments.presentation.CommentSort
import com.dertefter.comments.presentation.Event
import com.dertefter.data.dto.comments.CommentDto
import com.dertefter.data.repository.CommentsRepository
import com.dertefter.navigation.Navigator
import com.jamal_aliev.paginator.MutableCursorPaginator
import com.jamal_aliev.paginator.extension.distinctBy
import com.jamal_aliev.paginator.extension.uiState
import com.jamal_aliev.paginator.extension.warmUpFromPersistent
import com.jamal_aliev.paginator.page.PaginatorUiState
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
class CommentsViewModel @Inject constructor(
    private val commentsRepository: CommentsRepository,
    private val navigator: Navigator
) : ViewModel() {

    val sorts = CommentSort.entries

    private val _selectedTab = MutableStateFlow(CommentSort.POPULAR)
    val selectedTab: StateFlow<CommentSort> = _selectedTab.asStateFlow()

    private val paginators = mutableMapOf<String, MutableCursorPaginator<CommentDto>>()
    private val _uiStates = mutableMapOf<String, StateFlow<PaginatorUiState<CommentDto>>>()

    private var currentPostId: String? = null

    fun getPaginator(postId: String, sort: CommentSort): MutableCursorPaginator<CommentDto> {
        currentPostId = postId
        val key = "$postId-${sort.value}"
        return paginators.getOrPut(key) {
            commentsRepository.getCommentsPaginator(postId, sort.value).also {
                setupPaginator(it)
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getUiState(postId: String, sort: CommentSort): StateFlow<PaginatorUiState<CommentDto>> {
        val key = "$postId-${sort.value}"
        return _uiStates.getOrPut(key) {
            getPaginator(postId, sort).uiState.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = PaginatorUiState.Idle
            )
        }
    }

    private fun setupPaginator(paginator: MutableCursorPaginator<CommentDto>) {
        viewModelScope.launch {
            paginator.warmUpFromPersistent()
            paginator.distinctBy { it.id }
            paginator.restart(silentlyLoading = true)
        }
    }

    fun onEvent(event: Event) {
        when (event) {

            is Event.OnLike -> {
                viewModelScope.launch {
                    commentsRepository.likeComment(event.commentId)
                }
            }
            is Event.OnUnlike -> {
                viewModelScope.launch {
                    commentsRepository.unlikeComment(event.commentId)
                }
            }
            is Event.OnTabSelected -> {
                if (_selectedTab.value != event.tab) {
                    _selectedTab.value = event.tab
                }
            }
            Event.OnLoadMore -> {
                viewModelScope.launch {
                    val postId = currentPostId ?: return@launch
                    val sort = _selectedTab.value
                    val key = "$postId-${sort.value}"
                    paginators[key]?.goNextPage()
                }
            }
            is Event.OnLoadMoreReplies -> {
                viewModelScope.launch {
                    commentsRepository.getReplies(event.commentId, null)
                }
            }
            is Event.OnRefresh -> {
                viewModelScope.launch {
                    val key = "${event.postId}-${event.tab.value}"
                    paginators[key]?.restart(silentlyLoading = false)
                }
            }
        }
    }

    override fun onCleared() {
        paginators.values.forEach { it.release() }
        super.onCleared()
    }
}
