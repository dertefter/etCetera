package com.dertefter.user


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dertefter.data.common.AppError
import com.dertefter.data.common.toAppError
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
import com.dertefter.user.usecase.OpenAsDialogUseCase
import com.dertefter.user.usecase.PinPostUseCase
import com.dertefter.user.usecase.SaveMeUseCase
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
import com.jamal_aliev.paginator.cursor.extension.uiState
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
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


@Suppress("unchecked_cast")
@OptIn(ExperimentalCoroutinesApi::class)
@ViewModelKey
@ContributesIntoMap(AppScope::class)
@Inject
class UserViewModel(
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
    private val openAsDialogUseCase: OpenAsDialogUseCase
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

    private val _paginators =
        MutableStateFlow<Map<FeedTab, MutableCursorPaginator<String, PostDto>>>(emptyMap())
    val paginators: StateFlow<Map<FeedTab, MutableCursorPaginator<String, PostDto>>> =
        _paginators.asStateFlow()

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

    private var initializedUserId: String? = null
    private var initializedUsername: String? = null

    fun initWithUserId(userId: String) {
        val cleanId = userId.removePrefix("@")
        if (initializedUserId == cleanId && isLoadingOrLoaded()) return
        initializedUsername = null
        initializedUserId = cleanId
        initJob?.cancel()
        initJob = viewModelScope.launch {
            loadUser(cleanId)
        }
    }

    fun initWithUsername(username: String) {
        val cleanUsername = username.removePrefix("@")
        if (initializedUsername == cleanUsername && isLoadingOrLoaded()) return
        initializedUsername = cleanUsername
        initializedUserId = null
        initJob?.cancel()
        initJob = viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val userId: String? = updateUserUseCase(cleanUsername)
                .getOrNull()?.id ?: getUserByUsernameUseCase(cleanUsername).first()?.id
            if (userId != null) {
                loadUser(userId)
            } else {
                initializedUsername = null
                _isLoading.value = false
                _error.value = AppError.ApiError(code = null, message = null)
            }

        }
    }

    private fun isLoadingOrLoaded() = initJob?.isActive == true || _paginators.value.isNotEmpty()

    private suspend fun loadUser(userId: String) {
        val cleanId = userId.removePrefix("@")
        _userId.value = cleanId
        update()
    }

    fun updateMe() {
        viewModelScope.launch {
            updateMeUseCase()
        }
    }

    private fun setupPaginator(paginator: MutableCursorPaginator<String, PostDto>) {
        viewModelScope.launch {
            paginator.restart(silentlyLoading = true)
        }
    }

    fun onEvent(event: Event) {
        when (event) {

            is Event.OnOpenPinChanger -> {
                openAsBottomSheetUseCase(Routes.PinChanger)
            }

            is Event.OnNavigateToAuth -> {
                navigateToScreenUseCase(Routes.Auth)
            }

            is Event.OnNavigateToSettings -> {
                navigateToScreenUseCase(Routes.Settings)
            }

            is Event.OnOpenAttachmentsViewer -> {
                openAsDialogUseCase(
                    Routes.AttachmentsViewer(
                        attachments = event.attachments.map { it.toNavigationModel() },
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
                if (userUiState.value.isMe) {
                    openAsBottomSheetUseCase(
                        Routes.NewPost()
                    )
                } else {
                    userUiState.value.userDto?.id.let { id ->
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
        val currentIdOrUsername = _userId.value ?: return
        _isLoading.value = true
        _error.value = null

        val updateUserResult = updateUserUseCase(currentIdOrUsername)
        val userDto = updateUserResult.getOrNull()

        val resolvedUserId = userDto?.id
            ?: getUserByUsernameUseCase(currentIdOrUsername).first()?.id
            ?: getUserByIdUseCase(currentIdOrUsername).first()?.id
            ?: currentIdOrUsername

        _userId.value = resolvedUserId

        if (userDto == null && getUserByIdUseCase(resolvedUserId).first() == null) {
            _isLoading.value = false
            _error.value = updateUserResult.exceptionOrNull()?.toAppError()
                ?: AppError.ApiError(code = null, message = null)
            return
        }

        val pinnedPostId: String? = userDto?.pinnedPostId

        _paginators.value.values.forEach { it.release() }
        _paginators.value = emptyMap()

        val map = tabs.associateWith { tab ->
            when (tab) {
                FeedTab.POSTS -> getPostsPaginatorUseCase(resolvedUserId, pinnedPostId = { pinnedPostId })
                FeedTab.LIKES -> getLikedPostsPaginatorUseCase(resolvedUserId)
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
