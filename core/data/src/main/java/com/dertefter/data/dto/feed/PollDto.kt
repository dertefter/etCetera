package com.dertefter.data.dto.feed

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PollDto(
    @SerialName("id") val id: String,
    @SerialName("postId") val postId: String,
    @SerialName("question") val question: String,
    @SerialName("multipleChoice") val multipleChoice: Boolean,
    @SerialName("options") val options: List<PollOptionDto>,
    @SerialName("totalVotes") val totalVotes: Int,
    @SerialName("hasVoted") val hasVoted: Boolean,
    @SerialName("votedOptionIds") val votedOptionIds: List<String> = emptyList(),
    @SerialName("createdAt") val createdAt: String
)

