package com.dertefter.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dertefter.data.dto.notifications.NotificationDto
import com.dertefter.data.repository.NotificationsRepository
import com.dertefter.navigation.Navigator
import com.dertefter.navigation.Routes
import com.dertefter.notifications.presentation.Event
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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationsRepository: NotificationsRepository,
    private val navigator: Navigator
) : ViewModel() {

    private val _selectedFilter = MutableStateFlow<String?>(null)
    val selectedFilter = _selectedFilter.asStateFlow()

    private var _paginator: MutableCursorPaginator<String, NotificationDto>? = null
    val paginator: MutableCursorPaginator<String, NotificationDto>
        get() = _paginator ?: notificationsRepository.getNotificationsPaginator().also {
            _paginator = it
            setupPaginator(it)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<PaginatorUiState<NotificationDto>> = _selectedFilter.flatMapLatest { filter ->
        _paginator?.release()
        val newPaginator = notificationsRepository.getNotificationsPaginator(filter)
        _paginator = newPaginator
        setupPaginator(newPaginator)
        newPaginator.uiState
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PaginatorUiState.Idle
    )

    private fun setupPaginator(paginator: MutableCursorPaginator<String, NotificationDto>) {
        viewModelScope.launch {
            paginator.distinctBy { it.id }
            paginator.prefetchController(
                scope = viewModelScope,
                prefetchDistance = 5
            )
            paginator.warmUpFromPersistent()
            paginator.restart(silentlyLoading = true)
        }
    }

    fun onEvent(event: Event) {
        when (event) {
            is Event.OnFilterChanged -> {
                _selectedFilter.value = event.type
            }
            is Event.OnOpenUser -> {
                navigator.navigate(Routes.User(event.userId))
            }
            is Event.OnOpenPost -> {
                navigator.navigate(Routes.Post(event.postId))
            }
            is Event.OnRefresh -> {
                viewModelScope.launch {
                    paginator.restart()
                }
            }
            is Event.OnNavigateBack -> {
                navigator.navigateUp()
            }
        }
    }

    override fun onCleared() {
        _paginator?.release()
    }
}
