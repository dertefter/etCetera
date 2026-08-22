package com.dertefter.switch_account.presentation

data class UiState(
    val loginHistory: List<String> = emptyList(),
    val currentLogin: String? = null
)
