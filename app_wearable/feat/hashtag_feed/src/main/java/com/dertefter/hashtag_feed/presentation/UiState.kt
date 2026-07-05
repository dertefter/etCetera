package com.dertefter.hashtag_feed.presentation

import com.dertefter.data.common.AppError
import com.dertefter.data.dto.feed.PostDto
import com.jamal_aliev.paginator.core.page.PaginatorUiState

data class UiState(
    val hashtag: String? = null,
    val uiState: PaginatorUiState<PostDto> = PaginatorUiState.Idle,
    val isLoading: Boolean = true,
    val error: AppError? = null
)
