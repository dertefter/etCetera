package com.dertefter.data.dto.feed

import kotlinx.serialization.Serializable

@Serializable
data class PollDto(
    val id: String,
    val postId: String,
    val question: String,
    val multipleChoice: Boolean,
    val options: List<PollOptionDto>,
    val totalVotes: Int,
    val hasVoted: Boolean,
    val votedOptionIds: List<String> = emptyList(),
    val createdAt: String
)

