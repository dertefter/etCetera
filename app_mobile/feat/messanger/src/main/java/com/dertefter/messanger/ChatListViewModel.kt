package com.dertefter.messanger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dertefter.data.dto.messenger.ChatDto
import com.dertefter.data.repository.MessangerRepository
import com.dertefter.navigation.Navigator
import com.dertefter.navigation.Routes
import com.dertefter.messanger.presentation.Event
import com.jamal_aliev.paginator.core.page.PaginatorUiState
import com.jamal_aliev.paginator.cursor.MutableCursorPaginator
import com.jamal_aliev.paginator.cursor.extension.refreshAll
import com.jamal_aliev.paginator.cursor.extension.uiState
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@ViewModelKey
@ContributesIntoMap(AppScope::class)
@Inject
class ChatListViewModel(
    private val messangerRepository: MessangerRepository,
    private val navigator: Navigator
) : ViewModel() {

    val paginator: MutableCursorPaginator<String, ChatDto> by lazy {
        messangerRepository.getChatsPaginator().also { setupPaginator(it) }
    }

    val uiState: StateFlow<PaginatorUiState<ChatDto>> by lazy {
        paginator.uiState.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PaginatorUiState.Idle
        )
    }

    private fun setupPaginator(paginator: MutableCursorPaginator<String, ChatDto>) {
        viewModelScope.launch {
            paginator.restart()
        }
    }

    fun onEvent(event: Event) {
        when (event) {
            is Event.OnOpenChat -> {
                navigator.navigate(Routes.Chat(event.chatId))
            }

            is Event.OnRefresh -> {
                viewModelScope.launch {
                    paginator.refreshAll()
                }
            }

            is Event.OnNavigateBack -> {
                navigator.navigateUp()
            }
        }
    }

    override fun onCleared() {
        paginator.release()
    }
}
