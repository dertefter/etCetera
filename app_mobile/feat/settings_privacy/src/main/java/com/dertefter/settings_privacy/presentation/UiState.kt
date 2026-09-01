package com.dertefter.settings_privacy.presentation

import com.dertefter.data.dto.me.PrivacyDto

data class UiState(
    val isLoading: Boolean = true,
    val privacy: PrivacyDto? = null
)
