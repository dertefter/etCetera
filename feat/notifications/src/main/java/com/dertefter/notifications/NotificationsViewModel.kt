package com.dertefter.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dertefter.data.dto.notifications.NotificationDto
import com.dertefter.data.repository.NotificationsRepository
import com.dertefter.notifications.presentation.Event
import com.dertefter.navigation.Navigator
import com.dertefter.navigation.Routes
import com.jamal_aliev.paginator.MutableCursorPaginator
import com.jamal_aliev.paginator.extension.distinctBy
import com.jamal_aliev.paginator.extension.prefetchController
import com.jamal_aliev.paginator.extension.uiState
import com.jamal_aliev.paginator.extension.warmUpFromPersistent
import com.jamal_aliev.paginator.page.PaginatorUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationsRepository: NotificationsRepository,
    private val navigator: Navigator
) : ViewModel() {

    val paginator: MutableCursorPaginator<NotificationDto> = notificationsRepository.getNotificationsPaginator()

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<PaginatorUiState<NotificationDto>> = paginator.uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PaginatorUiState.Idle
    )

    init {
        setupPaginator()
    }

    private fun setupPaginator() {
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
        paginator.release()
        super.onCleared()
    }

}
