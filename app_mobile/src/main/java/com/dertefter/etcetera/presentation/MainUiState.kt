package com.dertefter.etcetera.presentation

import com.dertefter.data.common.AppError

data class MainUiState(
    val isReady: Boolean = false,
    val currentLogin: String? = null,
    val notificationCount: Int? = null,
    val meUserId: String? = null,
    val currentError: AppError? = null
)