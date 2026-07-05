package com.dertefter.auth.presentation

import com.dertefter.data.common.AppError

data class UiState(
    val login: String = "",
    val isLoginValid: Boolean = true,
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val error: AppError? = null,
    val isTurnstileVisible: Boolean = false
)