package com.dertefter.messanger.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dertefter.data.dto.messenger.MessageDto
import com.dertefter.data.repository.MeRepository
import com.dertefter.data.repository.MessangerRepository
import com.dertefter.design.components.post.AttachmentUiModel
import com.dertefter.navigation.AttachmentNavigationModel
import com.dertefter.navigation.Navigator
import com.dertefter.navigation.Routes
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
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

fun AttachmentUiModel.toNavigationModel() = AttachmentNavigationModel(id, type, url, mimeType)

@ViewModelKey
@ContributesIntoMap(AppScope::class)
@Inject
class ChatViewModel(
    private val messangerRepository: MessangerRepository,
    private val meRepository: MeRepository,
    private val navigator: Navigator
) : ViewModel() {

    val meUserId: StateFlow<String?> = meRepository.me
        .map { it?.id }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    private val paginators = mutableMapOf<String, MutableCursorPaginator<String, MessageDto>>()
    private val uiStates = mutableMapOf<String, StateFlow<PaginatorUiState<MessageDto>>>()

    fun getPaginator(chatId: String): MutableCursorPaginator<String, MessageDto> {
        return paginators.getOrPut(chatId) {
            messangerRepository.getMessagesPaginatorForChat(chatId).also { setupPaginator(it) }
        }
    }

    fun getUiState(chatId: String): StateFlow<PaginatorUiState<MessageDto>> {
        return uiStates.getOrPut(chatId) {
            getPaginator(chatId).uiState.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = PaginatorUiState.Idle
            )
        }
    }

    private fun setupPaginator(paginator: MutableCursorPaginator<String, MessageDto>) {
        viewModelScope.launch {
            paginator.restart()
        }
    }

    fun onEvent(event: ChatEvent, chatId: String) {
        when (event) {
            ChatEvent.OnRefresh -> {
                viewModelScope.launch {
                    getPaginator(chatId).refreshAll()
                }
            }
            ChatEvent.OnNavigateBack -> {
                navigator.navigateUp()
            }
            is ChatEvent.OnOpenAttachmentsViewer -> {
                navigator.navigate(
                    Routes.AttachmentsViewer(
                        attachments = event.attachments.map { it.toNavigationModel() },
                        viewPosition = event.position
                    )
                )
            }
        }
    }

    override fun onCleared() {
        paginators.values.forEach { it.release() }
        paginators.clear()
        uiStates.clear()
    }
}
