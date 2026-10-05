package com.dertefter.etcetera.presentation

import com.dertefter.data.common.AppError
import com.dertefter.data.dto.event.CurrentEvent

data class MainUiState(
    val isReady: Boolean = false,
    val currentLogin: String? = null,
    val notificationCount: Int? = null,
    val meUserId: String? = null,
    val currentError: AppError? = null,
    val currentEvent: CurrentEvent? = null
)