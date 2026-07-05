package com.dertefter.data.dto.comments

import com.dertefter.data.dto.feed.PaginationPostsDto
import com.dertefter.data.dto.feed.PostDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RepliesDataDto(
    @SerialName("replies") val replies: List<CommentDto>,
    @SerialName("pagination") val pagination: PaginationRepliesDto
)