package com.dertefter.data.dto.comments

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentsDataDto(
    @SerialName("comments") val comments: List<CommentDto>,
    @SerialName("hasMore") val hasMore: Boolean,
    @SerialName("nextCursor") val nextCursor: String?,
    @SerialName("total") val total: Int?

)
