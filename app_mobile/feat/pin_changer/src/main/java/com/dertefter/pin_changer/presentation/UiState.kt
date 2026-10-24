package com.dertefter.pin_changer.presentation

import com.dertefter.data.dto.feed.Pin
import com.dertefter.data.dto.me.Me

data class UiState(
    val me: Me? = null,
    val isLoading: Boolean = false,
    val pins: List<Pin> = emptyList()
)
