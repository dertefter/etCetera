package com.dertefter.data.dto.feed

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PollOptionDto(
    @SerialName("id") val id: String,
    @SerialName("text") val text: String,
    @SerialName("position") val position: Int,
    @SerialName("votesCount") val votesCount: Int
)
