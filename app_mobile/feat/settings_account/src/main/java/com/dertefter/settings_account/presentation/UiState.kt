package com.dertefter.settings_account.presentation

import com.dertefter.data.dto.feed.Pin
import com.dertefter.data.dto.me.Me

data class UiState(
    val currentLogin: String? = null,
    val me: Me? = null,
    val isLoading: Boolean = false,
    val canSave: Boolean = false,
    val displayNameInput: String = "",
    val usernameInput: String = "",
    val bioInput: String = "",
    val pins: List<Pin> = emptyList()
)
