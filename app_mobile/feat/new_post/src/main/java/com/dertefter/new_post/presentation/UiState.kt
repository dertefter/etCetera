package com.dertefter.new_post.presentation

import com.dertefter.data.dto.feed.PostDto
import com.dertefter.design.components.poll.NewPollUiModel
import com.dertefter.design.components.post.SpanUiModel

data class UiState(
    val content: String,
    val spans: List<SpanUiModel> = emptyList(),
    val uploads: List<Upload>,
    val poll: NewPollUiModel? = null,
    val isUploadingPost: Boolean = false,
    val originalPost: PostDto? = null
)