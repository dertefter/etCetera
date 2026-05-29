package com.dertefter.user.presentation

import com.dertefter.data.common.AppError
import com.dertefter.data.dto.feed.PostDto
import com.dertefter.data.dto.user.UserDto
import com.jamal_aliev.paginator.page.PaginatorUiState

data class UiState(
    val userDto: UserDto? = null,
    val isMe: Boolean = false,
    val selectedTab: FeedTab = FeedTab.POSTS,
    val uiStates: Map<FeedTab, PaginatorUiState<PostDto>> = emptyMap(),
    val isLoading: Boolean = true,
    val error: AppError? = null
)
