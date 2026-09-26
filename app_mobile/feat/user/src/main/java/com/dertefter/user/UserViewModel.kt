package com.dertefter.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dertefter.data.common.AppError
import com.dertefter.data.dto.feed.PostDto
import com.dertefter.data.dto.me.UpdateMeRequestDto
import com.dertefter.data.dto.user.UserDto
import com.dertefter.navigation.Routes
import com.dertefter.user.presentation.Event
import com.dertefter.user.presentation.FeedTab
import com.dertefter.user.presentation.UserUiState
import com.dertefter.user.presentation.mapper.toNavigationModel
import com.dertefter.user.usecase.BlockUserUseCase
import com.dertefter.user.usecase.DeletePostUseCase
import com.dertefter.user.usecase.FollowUserUseCase
import com.dertefter.user.usecase.GetLikedPostsPaginatorUseCase
import com.dertefter.user.usecase.GetMeUseCase
import com.dertefter.user.usecase.GetPostsPaginatorUseCase
import com.dertefter.user.usecase.GetUserByIdUseCase
import com.dertefter.user.usecase.GetUserByUsernameUseCase
import com.dertefter.user.usecase.LikePostUseCase
import com.dertefter.user.usecase.NavigateBackUseCase
import com.dertefter.user.usecase.NavigateToScreenUseCase
import com.dertefter.user.usecase.OpenAsBottomSheetUseCase
import com.dertefter.user.usecase.PinPostUseCase
import com.dertefter.user.usecase.SaveMeUseCase
import com.dertefter.user.usecase.StoleBannerUseCase
import com.dertefter.user.usecase.UnblockUserUseCase
import com.dertefter.user.usecase.UnfollowUserUseCase
import com.dertefter.user.usecase.UnlikePostUseCase
import com.dertefter.user.usecase.UnpinPostUseCase
import com.dertefter.user.usecase.UpdateMeUseCase
import com.dertefter.user.usecase.UpdatePostStatsUseCase
import com.dertefter.user.usecase.UpdateUserUseCase
import com.dertefter.user.usecase.VotePollUseCase
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
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
    getMeUseCase: GetMeUseCase,
    private val updateMeUseCase: UpdateMeUseCase,
    private val saveMeUseCase: SaveMeUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val getUserByUsernameUseCase: GetUserByUsernameUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val followUserUseCase: FollowUserUseCase,
    private val unfollowUserUseCase: UnfollowUserUseCase,
    private val blockUserUseCase: BlockUserUseCase,
    private val unblockUserUseCase: UnblockUserUseCase,
    private val pinPostUseCase: PinPostUseCase,
    private val unpinPostUseCase: UnpinPostUseCase,
    private val likePostUseCase: LikePostUseCase,
    private val unlikePostUseCase: UnlikePostUseCase,
    private val updatePostStatsUseCase: UpdatePostStatsUseCase,
    private val deletePostUseCase: DeletePostUseCase,
    private val votePollUseCase: VotePollUseCase,
    private val getPostsPaginatorUseCase: GetPostsPaginatorUseCase,
    private val getLikedPostsPaginatorUseCase: GetLikedPostsPaginatorUseCase,
    private val navigateToScreenUseCase: NavigateToScreenUseCase,
    private val openAsBottomSheetUseCase: OpenAsBottomSheetUseCase,
    private val navigateBackUseCase: NavigateBackUseCase,
    private val stoleBannerUseCase: StoleBannerUseCase
) : ViewModel() {

    private val _meUserId = getMeUseCase().map {
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
                getUserByIdUseCase(userId),
                _isMe,
                _isLoading,
                _error
            ) { args ->
                UserUiState(
                    userDto = args[0] as UserDto?,
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

    fun initWithUserId(userId: String) {
        initJob?.cancel()
        initJob = viewModelScope.launch {
            _userId.value = userId
            update()
        }
    }

    fun initWithUsername(username: String) {
        initJob?.cancel()
        initJob = viewModelScope.launch {
            _isLoading.value = true
            val userId: String? = updateUserUseCase(username)
                .getOrNull()?.id ?: getUserByUsernameUseCase(username).first()?.id
            if (userId != null){
                initWithUserId(userId)
            } else {
                _isLoading.value = false
                _error.value = AppError.ApiError(code = null, message = null)
            }

        }
    }

    fun updateMe(){
        viewModelScope.launch {
            updateMeUseCase()
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
                paginator.refreshAll()
            } else {
                paginator.restart(silentlyLoading = true)
            }
        }
    }

    fun onEvent(event: Event) {
        when (event) {

            is Event.OnStoleBanner -> {
                viewModelScope.launch {
                    stoleBannerUseCase(event.banner)
                }
            }

            is Event.OnNavigateToAuth -> {
                navigateToScreenUseCase(Routes.Auth)
            }

            Event.OnOpenSwitchAccount -> {
                openAsBottomSheetUseCase(Routes.SwitchAccount)
            }

            is Event.OnNavigateToSettings -> {
                navigateToScreenUseCase(Routes.Settings)
            }

            is Event.OnOpenAttachmentsViewer -> {
                navigateToScreenUseCase(
                    Routes.AttachmentsViewer(
                        attachments = event.attachments.map {it.toNavigationModel()},
                        viewPosition = event.position
                    )
                )
            }

            is Event.OnPin -> {
                viewModelScope.launch {
                    pinPostUseCase(event.postId)
                }
            }

            is Event.OnUnpin -> {
                viewModelScope.launch {
                    unpinPostUseCase(event.postId)
                }
            }
            
            is Event.OnOpenPost -> {
                navigateToScreenUseCase(Routes.Post(event.postId))
            }

            Event.OnNavigateBack -> {
                navigateBackUseCase()
            }

            is Event.OnLike -> {
                viewModelScope.launch {
                    likePostUseCase(event.postId)
                }
            }

            is Event.OnUnlike -> {
                viewModelScope.launch {
                    unlikePostUseCase(event.postId)
                }
            }

            is Event.OnUpdateStats -> {
                viewModelScope.launch {
                    if (event.ids.isNotEmpty()) {
                        updatePostStatsUseCase(event.ids)
                    }
                }
            }

            is Event.OnTabSelected -> {
               _selectedTab.value = event.tab
            }

            is Event.OnRefresh -> {
                viewModelScope.launch {
                    update()
                    updateMe()
                }
            }

            is Event.OnNavigateToComments -> {
                openAsBottomSheetUseCase(Routes.Comments(event.postId))
            }

            is Event.OnBlock -> {
                viewModelScope.launch {
                    if (event.isBlocked) {
                        blockUserUseCase(event.userId)
                    } else {
                        unblockUserUseCase(event.userId)
                    }
                }
            }

            is Event.OnFollow -> {
                follow(event.userId)
            }

            is Event.OnBannerEdit -> {
                navigateToScreenUseCase(Routes.BannerEdit)
            }

            is Event.OnSaveBio -> {
                viewModelScope.launch {
                    saveMeUseCase(
                        UpdateMeRequestDto(bio = event.bio)
                    )
                }

            }

            is Event.OnOpenUser -> {
                navigateToScreenUseCase(
                    Routes.User(event.userId)
                )
            }

            is Event.OnDeletePost -> {
                viewModelScope.launch {
                    deletePostUseCase(event.postId)
                }
            }

            is Event.OnOpenNewPost -> {
                if (userUiState.value.isMe){
                    openAsBottomSheetUseCase(
                        Routes.NewPost()
                    )
                }else{
                    userUiState.value.userDto?.id.let{ id ->
                        openAsBottomSheetUseCase(
                            Routes.NewPost(wallRecipientId = id)
                        )
                    }
                }
            }

            is Event.OnUnfollow -> {
                unfollow(event.userId)
            }

            is Event.OnOpenFollowers -> {
                navigateToScreenUseCase(
                    Routes.Followers(event.userId, false)
                )
            }

            is Event.OnRepost -> {
                openAsBottomSheetUseCase(
                    Routes.Repost(postIdForRepost = event.postId)
                )
            }

            is Event.OnEditPost -> {
                openAsBottomSheetUseCase(
                    Routes.EditPost(postId = event.postId)
                )
            }

            is Event.OnOpenFollowing -> {
                navigateToScreenUseCase(
                    Routes.Followers(event.userId, true)
                )
            }

            is Event.OnVote -> {
                viewModelScope.launch {
                    votePollUseCase(event.postId, event.optionIds)
                }
            }

            is Event.OnOpenHashtag -> {
                navigateToScreenUseCase(Routes.HashtagFeed(event.name))
            }

            is Event.OnReport -> {
                openAsBottomSheetUseCase(
                    Routes.Report(targetType = event.targetType, targetId = event.targetId)
                )
            }
        }
    }

    private fun follow(userId: String) {
        viewModelScope.launch {
            followUserUseCase(userId)
        }
    }

    private fun unfollow(userId: String) {
        viewModelScope.launch {
            unfollowUserUseCase(userId)
        }
    }

    private suspend fun update() {
        val userId = _userId.value ?: return
        _isLoading.value = true
        _error.value = null

        val pinnedPostId: String? = updateUserUseCase(userId)
            .getOrNull()
            ?.pinnedPostId

        _paginators.value.values.forEach { it.release() }
        _paginators.value = emptyMap()

        val map = tabs.associateWith { tab ->
            when (tab) {
                FeedTab.POSTS -> getPostsPaginatorUseCase(userId, pinnedPostId = { pinnedPostId })
                FeedTab.LIKES -> getLikedPostsPaginatorUseCase(userId)
            }
        }
        _paginators.value = map
        map.values.forEach { setupPaginator(it) }



        _isLoading.value = false
    }

    override fun onCleared() {
        _paginators.value.values.forEach { it.release() }
    }
}
