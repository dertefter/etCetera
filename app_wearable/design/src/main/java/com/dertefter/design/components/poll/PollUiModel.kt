package com.dertefter.design.components.poll

data class PollUiModel(
    val id: String,
    val title: String,
    val options: List<PollOptionUiModel>,
    val totalCount: Int,
    val isMultipleChoice: Boolean
)
