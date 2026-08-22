package com.dertefter.settings_account.presentation

import com.dertefter.data.dto.me.MeDto

data class UiState(
    val currentLogin: String? = null,
    val me: MeDto? = null,
    val isLoading: Boolean = false,
    val canSave: Boolean = false,
    val displayNameInput: String = "",
    val usernameInput: String = "",
    val bioInput: String = ""
)
