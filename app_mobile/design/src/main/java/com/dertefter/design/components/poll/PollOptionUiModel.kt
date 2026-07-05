package com.dertefter.design.components.poll

data class PollOptionUiModel(
    val text: String,
    val id: String,
    val votesCount: Int = 0,
    val isChecked: Boolean
)