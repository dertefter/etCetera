package com.dertefter.settings_ai.presentation

import com.dertefter.ai.model.AiError
import com.dertefter.ai.model.AiModel

data class UiState(

    val isLoading: Boolean = true,
    val models: List<AiModel> = emptyList(),
    val activeModelId: String? = null,


    val isImporting: Boolean = false,
    val pendingDelete: AiModel? = null,

    val error: AiError? = null,
)
