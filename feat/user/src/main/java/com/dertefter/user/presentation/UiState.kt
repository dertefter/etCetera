package com.dertefter.user.presentation

import com.dertefter.data.common.AppError
import com.dertefter.data.dto.user.UserDto

data class UserUiState(
    val userDto: UserDto? = null,
    val isMe: Boolean = false,
    val isLoading: Boolean = true,
    val error: AppError? = null
)
