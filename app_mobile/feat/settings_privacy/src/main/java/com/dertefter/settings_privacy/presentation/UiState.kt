package com.dertefter.settings_privacy.presentation

import com.dertefter.data.dto.me.Privacy

data class UiState(
    val isLoading: Boolean = true,
    val privacy: Privacy? = null
)
