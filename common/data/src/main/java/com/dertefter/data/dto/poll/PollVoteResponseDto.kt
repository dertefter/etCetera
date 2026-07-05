package com.dertefter.data.dto.poll

import com.dertefter.data.dto.feed.PollDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PollVoteResponseDto(
    @SerialName("data") val data: PollDto
)