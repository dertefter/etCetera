package com.dertefter.comments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dertefter.comments.presentation.CommentSort
import com.dertefter.comments.presentation.Event
import com.dertefter.data.dto.comments.CommentDto
import com.dertefter.data.repository.CommentsRepository
import com.dertefter.data.repository.MeRepository
import com.dertefter.navigation.Navigator
import com.dertefter.navigation.Routes
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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentsViewModel @Inject constructor(
    private val commentsRepository: CommentsRepository,
    meRepository: MeRepository,
    private val navigator: Navigator
) : ViewModel() {

    val meUserId: StateFlow<String?> = meRepository.me
        .map { it?.id }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    private val paginators = mutableMapOf<String, MutableCursorPaginator<String, CommentDto>>()
    private val _uiStates = mutableMapOf<String, StateFlow<PaginatorUiState<CommentDto>>>()

    private var currentPostId: String? = null

    fun getPaginator(postId: String): MutableCursorPaginator<String, CommentDto> {
        currentPostId = postId
        val key = postId
        return paginators.getOrPut(key) {
            commentsRepository.getCommentsPaginator(postId, CommentSort.POPULAR.value).also {
                setupPaginator(it)
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getUiState(postId: String): StateFlow<PaginatorUiState<CommentDto>> {
        val key = postId
        return _uiStates.getOrPut(key) {
            getPaginator(postId).uiState.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = PaginatorUiState.Idle
            )
        }
    }

    private fun setupPaginator(paginator: MutableCursorPaginator<String, CommentDto>) {
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

            is Event.OnDeleteComment -> {
                viewModelScope.launch {
                    commentsRepository.deleteComment(commentId = event.commentId)
                }
            }

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
            Event.OnLoadMore -> {
                viewModelScope.launch {
                    val postId = currentPostId ?: return@launch
                    paginators[postId]?.goNextPage()
                }
            }
            is Event.OnLoadMoreReplies -> {
                viewModelScope.launch {
                    commentsRepository.getReplies(event.commentId, null)
                }
            }
            is Event.OnRefresh -> {
                viewModelScope.launch {
                    paginators[event.postId]?.restart(silentlyLoading = true)
                }
            }

            is Event.OnOpenUser -> {
                navigator.navigate(
                    Routes.User(event.userId)
                )
            }

        }
    }

    override fun onCleared() {
        paginators.values.forEach { it.release() }
    }
}
