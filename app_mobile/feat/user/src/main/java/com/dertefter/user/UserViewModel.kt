package com.dertefter.user

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dertefter.data.common.AppError
import com.dertefter.data.common.toAppError
import com.dertefter.data.dto.feed.PostDto
import com.dertefter.data.dto.me.UpdateMeRequestDto
import com.dertefter.data.repository.FeedRepository
import com.dertefter.data.repository.MeRepository
import com.dertefter.data.repository.PostRepository
import com.dertefter.data.repository.UserRepository
import com.dertefter.navigation.Navigator
import com.dertefter.navigation.Routes
import com.dertefter.user.presentation.Event
import com.dertefter.user.presentation.FeedTab
import com.dertefter.user.presentation.UserUiState
import com.dertefter.user.presentation.mapper.toNavigationModel
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@Suppress("unchecked_cast")
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val meRepository: MeRepository,
    private val feedRepository: FeedRepository,
    private val postRepository: PostRepository,
    private val navigator: Navigator
) : ViewModel() {

    private val _meUserId = meRepository.me.map {
        it?.id
    }.distinctUntilChanged()

    private val _userId = MutableStateFlow<String?>(null)
    private val _isLoading = MutableStateFlow(true)
    private val _error = MutableStateFlow<AppError?>(null)

    private val _selectedTab = MutableStateFlow(FeedTab.POSTS)
    val selectedTab: StateFlow<FeedTab> = _selectedTab.asStateFlow()

    private val _isMe = combine(_meUserId, _userId) { meId, userId ->
        meId != null && meId == userId
    }.distinctUntilChanged()

    val tabs = FeedTab.entries

    private val _paginators = MutableStateFlow<Map<FeedTab, MutableCursorPaginator<String, PostDto>>>(emptyMap())
    val paginators: StateFlow<Map<FeedTab, MutableCursorPaginator<String, PostDto>>> = _paginators.asStateFlow()

    fun getPaginator(tab: FeedTab) = _paginators.value[tab]

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiStates: Map<FeedTab, StateFlow<PaginatorUiState<PostDto>>> = tabs.associateWith { tab ->
        _paginators.flatMapLatest { paginatorsMap ->
            paginatorsMap[tab]?.uiState ?: flowOf(PaginatorUiState.Idle)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PaginatorUiState.Idle
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val userUiState: StateFlow<UserUiState> = _userId.flatMapLatest { userId ->
        if (userId == null) {
            flowOf(UserUiState())
        } else {
            combine(
                userRepository.getUser(userId),
                _isMe,
                _isLoading,
                _error
            ) { args ->
                UserUiState(
                    userDto = args[0] as com.dertefter.data.dto.user.UserDto?,
                    isMe = args[1] as Boolean,
                    isLoading = args[2] as Boolean,
                    error = args[3] as AppError?
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserUiState()
    )


    private var initJob: Job? = null

    private val _currentPinnedPostId = MutableStateFlow<String?>(null)

    init {
        viewModelScope.launch {
            _userId.flatMapLatest { id ->
                if (id == null) flowOf(null)
                else userRepository.getUser(id).map { it?.pinnedPostId }
            }.collect { _currentPinnedPostId.value = it }
        }

        viewModelScope.launch {
            _userId.collectLatest { userId ->
                _paginators.value.values.forEach { it.release() }
                _paginators.value = emptyMap()
                if (userId == null) return@collectLatest

                val map = tabs.associateWith { tab ->
                    when (tab) {
                        FeedTab.POSTS -> feedRepository.getPostsPaginator(userId, pinnedPostId = { _currentPinnedPostId.value })
                        FeedTab.LIKES -> feedRepository.getLikedPostsPaginator(userId)
                    }
                }
                _paginators.value = map
                map.values.forEach { setupPaginator(it) }
            }
        }
    }

    fun initWithUserId(userId: String) {
        initJob?.cancel()
        initJob = viewModelScope.launch {
            _userId.value = userId
            update()
        }
    }

    fun updateMe(){
        viewModelScope.launch {
            meRepository.updateMe()
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

            is Event.OnNavigateToAuth -> {
                navigator.navigate(Routes.Auth)
            }

            Event.OnOpenSwitchAccount -> {
                navigator.openAsBottomSheet(Routes.SwitchAccount)
            }

            is Event.OnNavigateToSettings -> {
                navigator.navigate(Routes.Settings)
            }

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

            is Event.OnTabSelected -> {
               _selectedTab.value = event.tab
            }

            Event.OnLoadMore -> {
                // Handled by prefetchController
            }

            is Event.OnRefresh -> {
                viewModelScope.launch {
                    update()
                    updateMe()
                    val paginator = getPaginator(event.tab)
                    paginator?.refreshAll()
                    paginator?.jump(CursorBookmark(prev = null, self = "initial", next = null))
                }
            }

            is Event.OnNavigateToComments -> {
                navigator.openAsBottomSheet(Routes.Comments(event.postId))
            }

            is Event.OnShare -> {
                // TODO
            }

            is Event.OnBlock -> {
                // TODO
            }

            is Event.OnFollow -> {
                follow(event.userId)
            }

            is Event.OnBannerEdit -> {
                navigator.navigate(Routes.BannerEdit)
            }

            is Event.OnSaveBio -> {
                viewModelScope.launch {
                    meRepository.saveMe(
                        UpdateMeRequestDto(bio = event.bio)
                    )
                }

            }

            is Event.OnOpenUser -> {
                navigator.navigate(
                    Routes.User(event.userId)
                )
            }

            is Event.OnDeletePost -> {
                viewModelScope.launch {
                    postRepository.deletePost(event.postId)
                }
            }

            is Event.OnOpenNewPost -> {
                if (userUiState.value.isMe){
                    navigator.openAsBottomSheet(
                        Routes.NewPost()
                    )
                }else{
                    userUiState.value.userDto?.id.let{ id ->
                        navigator.openAsBottomSheet(
                            Routes.NewPost(wallRecipientId = id)
                        )
                    }
                }


            }

            is Event.OnUnfollow -> {
                unfollow(event.userId)
            }

            is Event.OnOpenFollowers -> {
                navigator.navigate(
                    Routes.Followers(event.userId, false)
                )
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

            is Event.OnOpenFollowing -> {
                navigator.navigate(
                    Routes.Followers(event.userId, true)
                )
            }

            is Event.OnVote -> {
                viewModelScope.launch {
                    postRepository.votePoll(event.postId, event.optionIds)
                }
            }

            is Event.OnOpenHashtag -> {
                navigator.navigate(Routes.HashtagFeed(event.name))
            }
        }
    }

    private fun follow(userId: String) {
        viewModelScope.launch {
            userRepository.follow(userId)
        }
    }

    private fun unfollow(userId: String) {
        viewModelScope.launch {
            userRepository.unfollow(userId)
        }
    }

    private suspend fun update() {
        val userId = _userId.value ?: return
        _isLoading.value = true
        _error.value = null
        userRepository.updateUser(userId).onSuccess { user ->
            _currentPinnedPostId.value = user.pinnedPostId
        }.onFailure {
            _error.value = it.toAppError()
        }
        _isLoading.value = false
    }

    override fun onCleared() {
        _paginators.value.values.forEach { it.release() }
    }
}
