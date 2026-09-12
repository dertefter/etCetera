package com.dertefter.report.presentation

data class UiState(
    val targetType: String? = null,
    val targetId: String? = null,
    val reason: String? = null,
    val description: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)
