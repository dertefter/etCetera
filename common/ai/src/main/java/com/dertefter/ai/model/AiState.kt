package com.dertefter.ai.model


sealed interface AiState {

    data object Idle : AiState

    data object Preparing : AiState

    data object Ready : AiState

    data class Failed(val error: AiError) : AiState
}
