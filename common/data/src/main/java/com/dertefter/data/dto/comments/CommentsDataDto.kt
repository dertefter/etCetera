package com.dertefter.data.dto.comments

import kotlinx.serialization.Serializable

@Serializable
data class CommentsDataDto(
    val comments: List<CommentDto>,
    val hasMore: Boolean,
    val nextCursor: String?,
    val total: Int?

)
