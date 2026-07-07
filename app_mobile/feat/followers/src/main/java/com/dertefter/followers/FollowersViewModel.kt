package com.dertefter.followers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dertefter.data.dto.followers.FollowerUserDto
import com.dertefter.data.repository.FollowersRepository
import com.dertefter.data.repository.UserRepository
import com.dertefter.followers.presentation.Event
import com.dertefter.followers.presentation.Tab
import com.dertefter.navigation.Navigator
import com.dertefter.navigation.Routes.*
import com.jamal_aliev.paginator.core.page.PaginatorUiState
import com.jamal_aliev.paginator.cursor.MutableCursorPaginator
import com.jamal_aliev.paginator.cursor.extension.distinctBy
import com.jamal_aliev.paginator.cursor.extension.prefetchController
import com.jamal_aliev.paginator.cursor.extension.uiState
import com.jamal_aliev.paginator.cursor.extension.warmUpFromPersistent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FollowersViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val followersRepository: FollowersRepository,
    private val navigator: Navigator
) : ViewModel() {

    private val _userId = MutableStateFlow<String?>(null)
    val userId: String get() = _userId.value ?: ""

    val tabs = Tab.entries

    private val _selectedTab = MutableStateFlow(Tab.FOLLOWERS)
    val selectedTab: StateFlow<Tab> = _selectedTab.asStateFlow()

    private val _paginators = MutableStateFlow<Map<Tab, MutableCursorPaginator<String, FollowerUserDto>>>(emptyMap())

    fun getPaginator(tab: Tab) = _paginators.value[tab]

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiStates: Map<Tab, StateFlow<PaginatorUiState<FollowerUserDto>>> = Tab.entries.associateWith { tab ->
        _paginators.flatMapLatest { paginatorsMap ->
            paginatorsMap[tab]?.uiState ?: flowOf(PaginatorUiState.Idle)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PaginatorUiState.Idle
        )
    }

    fun init(userId: String, startTabIsFollowing: Boolean) {
        if (_userId.value == userId) return
        _userId.value = userId
        _selectedTab.value = if (startTabIsFollowing) Tab.FOLLOWING else Tab.FOLLOWERS

        _paginators.value.values.forEach { it.release() }
        val map = mapOf(
            Tab.FOLLOWERS to followersRepository.getFollowersPaginator(userId),
            Tab.FOLLOWING to followersRepository.getFollowingPaginator(userId)
        )
        _paginators.value = map
        map.values.forEach {
            setupPaginator(it)
        }
    }

    private fun setupPaginator(paginator: MutableCursorPaginator<String, FollowerUserDto>) {
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
            is Event.OnTabSelected -> {
                _selectedTab.value = event.tab
            }
            is Event.OnRefresh -> {
                viewModelScope.launch {
                    _paginators.value[event.tab]?.restart(silentlyLoading = true)
                }
            }
            Event.OnBackClick -> {
                navigator.navigateUp()
            }
            is Event.OnOpenUser -> {
                navigator.navigate(User(event.userId))
            }
            is Event.OnFollow -> {
                viewModelScope.launch {
                    userRepository.follow(event.userId)
                }
            }
            is Event.OnUnfollow -> {
                viewModelScope.launch {
                    userRepository.unfollow(event.userId)
                }
            }
        }
    }

    override fun onCleared() {
        _paginators.value.values.forEach { it.release() }
    }
}
