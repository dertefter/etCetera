package com.dertefter.data.dto.poll

import com.dertefter.data.dto.feed.PollDto
import kotlinx.serialization.Serializable

@Serializable
data class PollVoteResponseDto(
    val data: PollDto
)