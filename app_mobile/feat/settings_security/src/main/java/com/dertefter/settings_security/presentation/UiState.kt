package com.dertefter.settings_security.presentation

import com.dertefter.data.dto.auth.AuthSessionDto

data class UiState(
    val isLoading: Boolean = true,
    val sessions: List<AuthSessionDto>?
)
