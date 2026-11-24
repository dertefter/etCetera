package com.dertefter.messanger.chat

import com.dertefter.design.components.post.AttachmentUiModel

sealed interface ChatEvent {
    data object OnRefresh : ChatEvent
    data object OnNavigateBack : ChatEvent
    data class OnOpenAttachmentsViewer(val attachments: List<AttachmentUiModel>, val position: Int = 0) : ChatEvent
}
