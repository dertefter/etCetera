package com.dertefter.user

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dertefter.data.common.AppError
import com.dertefter.data.common.toAppError
import com.dertefter.data.dto.feed.PostDto
import com.dertefter.data.dto.me.UpdateMeRequestDto
import com.dertefter.data.repository.MeRepository
import com.dertefter.data.repository.UserRepository
import com.dertefter.navigation.Navigator
import com.dertefter.navigation.Routes
import com.dertefter.navigation.Routes.Comments
import com.dertefter.user.presentation.Event
import com.dertefter.user.presentation.FeedTab
import com.dertefter.user.presentation.UiState
import com.dertefter.user.presentation.mapper.toNavigationModel
import com.jamal_aliev.paginator.MutableCursorPaginator
import com.jamal_aliev.paginator.extension.distinctBy
import com.jamal_aliev.paginator.extension.prefetchController
import com.jamal_aliev.paginator.extension.uiState
import com.jamal_aliev.paginator.extension.warmUpFromPersistent
import com.jamal_aliev.paginator.page.PaginatorUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val meRepository: MeRepository,
    private val navigator: Navigator
) : ViewModel() {

    private val _meUserId = meRepository.meDto.map {
        it?.id
    }.distinctUntilChanged()

    private val _userId = MutableStateFlow<String?>(null)
    private val _isLoading = MutableStateFlow(true)
    private val _error = MutableStateFlow<AppError?>(null)
    private val _selectedTab = MutableStateFlow(FeedTab.POSTS)

    private val _isMe = combine(_meUserId, _userId) { meId, userId ->
        meId != null && meId == userId
    }.distinctUntilChanged()

    val tabs = FeedTab.entries

    private val _paginators = MutableStateFlow<Map<FeedTab, MutableCursorPaginator<PostDto>>>(emptyMap())
    val paginators: StateFlow<Map<FeedTab, MutableCursorPaginator<PostDto>>> = _paginators.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _uiStates = _paginators.flatMapLatest { paginatorsMap ->
        if (paginatorsMap.isEmpty()) {
            flowOf(emptyMap<FeedTab, PaginatorUiState<PostDto>>())
        } else {
            combine(paginatorsMap.map { (tab, paginator) ->
                paginator.uiState.map { tab to it }
            }) { pairs ->
                pairs.toMap()
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<UiState> = _userId.flatMapLatest { userId ->
        if (userId == null) {
            flowOf(UiState())
        } else {
            combine(
                userRepository.getUser(userId),
                _isMe,
                _selectedTab,
                _uiStates,
                combine(_isLoading, _error) { isLoading, error -> isLoading to error }
            ) { user, isMe, selectedTab, uiStates, loadingAndError ->
                UiState(
                    userDto = user,
                    isMe = isMe,
                    selectedTab = selectedTab,
                    uiStates = uiStates,
                    isLoading = loadingAndError.first,
                    error = loadingAndError.second
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UiState()
    )

    private var initJob: Job? = null

    init {
        viewModelScope.launch {
            _userId.collect {
                _paginators.value = emptyMap()
            }
        }
        viewModelScope.launch {
            _userId.filterNotNull().flatMapLatest { id ->
                userRepository.getUser(id)
            }.collect { user ->
                if (user != null && _paginators.value.isEmpty()) {
                    val map = tabs.associateWith { tab ->
                        when (tab) {
                            FeedTab.POSTS -> userRepository.getPostsPaginator(user.id)
                            FeedTab.LIKES -> userRepository.getLikedPostsPaginator(user.id)
                        }
                    }
                    _paginators.value = map
                    map.values.forEach { setupPaginator(it) }
                }
            }
        }
    }

    fun initWithUserId(userId: String?) {
        initJob?.cancel()
        initJob = viewModelScope.launch {
            if (userId != null) {
                _userId.value = userId
                update()
            } else {
                _meUserId.collect { id ->
                    if (id == null) {
                        _isLoading.value = true
                        meRepository.fetchMe().onFailure {
                            _error.value = it.toAppError()
                            _isLoading.value = false
                        }
                    } else {
                        if (_userId.value != id) {
                            _userId.value = id
                            update()
                        } else {
                            _isLoading.value = false
                        }
                    }
                }
            }
        }
    }

    fun updateMe(){
        viewModelScope.launch {
            meRepository.fetchMe()
        }
    }

    private fun setupPaginator(paginator: MutableCursorPaginator<PostDto>) {
        viewModelScope.launch {
            paginator.distinctBy { it.id }
            paginator.prefetchController(
                scope = viewModelScope,
                prefetchDistance = 3
            )
            paginator.warmUpFromPersistent()
            paginator.restart(silentlyLoading = true)
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

            is Event.OnOpenPost -> {
                navigator.navigate(Routes.Post(event.postId))
            }

            Event.OnNavigateBack -> {
                navigator.navigateUp()
            }

            is Event.OnLike -> {
                viewModelScope.launch {
                    userRepository.likePost(event.postId)
                }
            }

            is Event.OnUnlike -> {
                viewModelScope.launch {
                    userRepository.unlikePost(event.postId)
                }
            }

            is Event.OnUpdateStats -> {
                viewModelScope.launch {
                    if (event.ids.isNotEmpty()) {
                        userRepository.updatePostStats(event.ids).onFailure {
                            Log.e("UserViewModel", it.stackTraceToString())
                        }
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
                update()
                updateMe()
                viewModelScope.launch {
                    _paginators.value[event.tab]?.restart()
                }
            }

            is Event.OnNavigateToComments -> {
                navigator.openAsBottomSheet(Comments(event.postId))
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
                    meRepository.updateMe(
                        UpdateMeRequestDto(bio = event.bio)
                    ).onFailure {
                        Log.e("OnSaveBio", it.stackTraceToString())
                    }
                }

            }

            is Event.OnOpenUser -> {
                navigator.navigate(
                    Routes.User(event.userId)
                )
            }

            is Event.OnOpenNewPost -> {
                uiState.value.userDto?.id.let{ id ->
                    navigator.openAsBottomSheet(
                        Routes.NewPost(id)
                    )
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

            is Event.OnOpenFollowing -> {
                navigator.navigate(
                    Routes.Followers(event.userId, true)
                )
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

    private fun update() {
        val userId = _userId.value ?: return
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            userRepository.updateUser(userId)
                .onFailure {
                    Log.e("UserViewModel", it.stackTraceToString())
                    _error.value = it.toAppError()
                }
            _isLoading.value = false
        }
    }

    override fun onCleared() {
        _paginators.value.values.forEach { it.release() }
        super.onCleared()
    }
}
