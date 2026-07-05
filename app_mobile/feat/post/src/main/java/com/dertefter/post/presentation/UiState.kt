package com.dertefter.post.presentation

import com.dertefter.design.components.post.PostUiModel

data class UiState(
    val post: PostUiModel? = null,
    val isLoading: Boolean = false
)
