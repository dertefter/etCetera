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
import com.jamal_aliev.paginator.cursor.extension.uiState
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


@ViewModelKey
@ContributesIntoMap(AppScope::class)
@Inject
class NotificationsViewModel(
    private val notificationsRepository: NotificationsRepository,
    private val navigator: Navigator
) : ViewModel() {

    private val _selectedFilter = MutableStateFlow<String?>(null)
    val selectedFilter = _selectedFilter.asStateFlow()

    private val paginators = mutableMapOf<String, MutableCursorPaginator<String, NotificationDto>>()
    private var _paginator: MutableCursorPaginator<String, NotificationDto>? = null

    val paginator: MutableCursorPaginator<String, NotificationDto>
        get() = _paginator ?: getOrCreatePaginator(selectedFilter.value)

    init {
        viewModelScope.launch {
            notificationsRepository.readAll()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<PaginatorUiState<NotificationDto>> =
        _selectedFilter.flatMapLatest { filter ->
            getOrCreatePaginator(filter).uiState
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PaginatorUiState.Idle
        )

    private fun getOrCreatePaginator(filter: String?): MutableCursorPaginator<String, NotificationDto> {
        val paginator = paginators.getOrPut(filter.orEmpty()) {
            notificationsRepository.getNotificationsPaginator(filter).also { setupPaginator(it) }
        }
        _paginator = paginator
        return paginator
    }

    private fun setupPaginator(paginator: MutableCursorPaginator<String, NotificationDto>) {
        viewModelScope.launch {
            paginator.restart()
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
                    notificationsRepository.readAll()
                }
            }

            is Event.OnNavigateBack -> {
                navigator.navigateUp()
            }
        }
    }

    override fun onCleared() {
        paginators.values.forEach { it.release() }
        paginators.clear()
        _paginator = null
    }
}
