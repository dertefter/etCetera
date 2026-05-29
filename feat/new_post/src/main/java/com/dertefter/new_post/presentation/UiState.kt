package com.dertefter.new_post.presentation

import com.dertefter.design.components.poll.NewPollUiModel

data class UiState(
    val content: String,
    val uploads: List<Upload>,
    val poll: NewPollUiModel? = null,
    val isUploadingPost: Boolean = false
)