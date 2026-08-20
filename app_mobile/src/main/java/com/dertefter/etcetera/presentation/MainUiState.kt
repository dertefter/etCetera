package com.dertefter.etcetera.presentation

data class MainUiState(
    val isReady: Boolean = false,
    val currentLogin: String? = null,
    val meUserId: String? = null,
    val currentError: com.dertefter.data.common.AppError? = null
)