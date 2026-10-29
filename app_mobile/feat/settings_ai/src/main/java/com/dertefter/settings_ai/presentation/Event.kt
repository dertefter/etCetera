package com.dertefter.settings_ai.presentation

import android.net.Uri
import com.dertefter.ai.model.AiModel

sealed interface Event {
    data object OnNavigateBack : Event

    data class OnImport(val uri: Uri) : Event

    data class OnSelect(val model: AiModel) : Event

    data class OnDeleteRequest(val model: AiModel) : Event

    data object OnDeleteConfirm : Event

    data object OnDeleteCancel : Event

    data object OnErrorShown : Event
}
