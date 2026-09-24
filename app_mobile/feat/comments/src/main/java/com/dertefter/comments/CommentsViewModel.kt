package com.dertefter.comments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dertefter.comments.presentation.CommentSort
import com.dertefter.comments.presentation.Event
import com.dertefter.comments.usecase.DeleteCommentUseCase
import com.dertefter.comments.usecase.GetCommentsPaginatorUseCase
import com.dertefter.comments.usecase.GetMeUseCase
import com.dertefter.comments.usecase.GetRepliesUseCase
import com.dertefter.comments.usecase.LikeCommentUseCase
import com.dertefter.comments.usecase.NavigateToScreenUseCase
import com.dertefter.comments.usecase.OpenAsBottomSheetUseCase
import com.dertefter.comments.usecase.UnlikeCommentUseCase
import com.dertefter.data.dto.comments.CommentDto
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentsViewModel @Inject constructor(
    getMeUseCase: GetMeUseCase,
    private val getCommentsPaginatorUseCase: GetCommentsPaginatorUseCase,
    private val deleteCommentUseCase: DeleteCommentUseCase,
    private val likeCommentUseCase: LikeCommentUseCase,
    private val unlikeCommentUseCase: UnlikeCommentUseCase,
    private val getRepliesUseCase: GetRepliesUseCase,
    private val openAsBottomSheetUseCase: OpenAsBottomSheetUseCase,
    private val navigateToScreenUseCase: NavigateToScreenUseCase
) : ViewModel() {

    val meUserId: StateFlow<String?> = getMeUseCase()
        .map { it?.id }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val sorts = CommentSort.entries

    private val _selectedTab = MutableStateFlow(CommentSort.POPULAR)
    val selectedTab: StateFlow<CommentSort> = _selectedTab.asStateFlow()

    private val paginators = mutableMapOf<String, MutableCursorPaginator<String, CommentDto>>()
    private val _uiStates = mutableMapOf<String, StateFlow<PaginatorUiState<CommentDto>>>()

    private var currentPostId: String? = null

    fun getPaginator(postId: String, sort: CommentSort): MutableCursorPaginator<String, CommentDto> {
        currentPostId = postId
        val key = "$postId-${sort.value}"
        return paginators.getOrPut(key) {
            getCommentsPaginatorUseCase(postId, sort.value).also {
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

    private fun setupPaginator(paginator: MutableCursorPaginator<String, CommentDto>) {
        viewModelScope.launch {
            paginator.distinctBy { it.id }
            paginator.prefetchController(
                scope = viewModelScope, prefetchDistance = 3
            )
            val inserted = paginator.warmUpFromPersistent()
            if (inserted > 0) {
                paginator.jump(CursorBookmark(prev = null, self = "initial", next = null))
                paginator.refreshAll(loadingSilently = true, finalSilently = true)
            } else {
                paginator.restart(silentlyLoading = true)
            }
        }
    }

    fun onEvent(event: Event) {
        when (event) {

            is Event.OnDeleteComment -> {
                viewModelScope.launch {
                    deleteCommentUseCase(commentId = event.commentId)
                }
            }

            is Event.OnNewComment -> {
                currentPostId?.let { postId ->
                    openAsBottomSheetUseCase(Routes.NewComment(postId = postId))
                }
            }

            is Event.OnReply -> {
                currentPostId?.let { postId ->
                    openAsBottomSheetUseCase(
                        Routes.NewCommentReply(
                            postId = postId,
                            commentId = event.commentId,
                            userId = event.userId
                        )
                    )
                }
            }

            is Event.OnLike -> {
                viewModelScope.launch {
                    likeCommentUseCase(event.commentId)
                }
            }

            is Event.OnUnlike -> {
                viewModelScope.launch {
                    unlikeCommentUseCase(event.commentId)
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
                    getRepliesUseCase(event.commentId, null)
                }
            }

            is Event.OnRefresh -> {
                viewModelScope.launch {
                    val key = "${event.postId}-${event.tab.value}"
                    paginators[key]?.restart(silentlyLoading = true)
                }
            }

            is Event.OnOpenUser -> {
                navigateToScreenUseCase(Routes.User(event.userId))
            }

            is Event.OnReport -> {
                openAsBottomSheetUseCase(
                    Routes.Report(targetType = "comment", event.commentId)
                )
            }

        }
    }

    override fun onCleared() {
        paginators.values.forEach { it.release() }
    }
}
