package com.dertefter.data.dto.comments

import kotlinx.serialization.Serializable

@Serializable
data class RepliesDataDto(
    val replies: List<CommentDto>,
    val pagination: PaginationRepliesDto
)