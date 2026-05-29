package com.dertefter.followers

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.dertefter.data.dto.followers.FollowerUserDto
import com.dertefter.data.repository.FollowersRepository
import com.dertefter.data.repository.UserRepository
import com.dertefter.followers.presentation.Event
import com.dertefter.followers.presentation.Tab
import com.dertefter.navigation.Navigator
import com.dertefter.navigation.Routes.*
import com.jamal_aliev.paginator.MutableCursorPaginator
import com.jamal_aliev.paginator.page.PaginatorUiState
import com.jamal_aliev.paginator.extension.uiState
import com.jamal_aliev.paginator.extension.warmUpFromPersistent
import com.jamal_aliev.paginator.extension.distinctBy
import com.jamal_aliev.paginator.extension.prefetchController
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
class FollowersViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
    private val followersRepository: FollowersRepository,
    private val navigator: Navigator
) : ViewModel() {

    private val args = savedStateHandle.toRoute<Followers>()
    val userId = args.userId

    val tabs = Tab.entries

    private val _selectedTab = MutableStateFlow(if (args.startTabIsFollowing) Tab.FOLLOWING else Tab.FOLLOWERS)
    val selectedTab: StateFlow<Tab> = _selectedTab.asStateFlow()

    private val paginators: Map<Tab, MutableCursorPaginator<FollowerUserDto>> = mapOf(
        Tab.FOLLOWERS to followersRepository.getFollowersPaginator(userId),
        Tab.FOLLOWING to followersRepository.getFollowingPaginator(userId)
    )

    fun getPaginator(tab: Tab) = paginators[tab]!!

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiStates: Map<Tab, StateFlow<PaginatorUiState<FollowerUserDto>>> = paginators.mapValues { (_, paginator) ->
        paginator.uiState.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PaginatorUiState.Idle
        )
    }

    init {
        paginators.values.forEach {
            setupPaginator(it)
        }
    }

    private fun setupPaginator(paginator: MutableCursorPaginator<FollowerUserDto>) {
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
                    paginators[event.tab]?.restart()
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
        paginators.values.forEach { it.release() }
        super.onCleared()
    }
}
