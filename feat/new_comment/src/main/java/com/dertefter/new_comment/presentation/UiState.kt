package com.dertefter.new_comment.presentation

import com.dertefter.design.components.post.SpanUiModel

data class UiState(
    val content: String,
    val spans: List<SpanUiModel> = emptyList(),
    val uploads: List<Upload>,
    val isUploadingComment: Boolean = false
)