package com.dertefter.data.dto.feed

import kotlinx.serialization.Serializable

@Serializable
data class PollOptionDto(
    val id: String,
    val text: String,
    val position: Int,
    val votesCount: Int
)
