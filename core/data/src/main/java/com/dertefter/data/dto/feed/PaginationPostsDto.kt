package com.dertefter.data.dto.feed

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaginationPostsDto(
    @SerialName("limit") val limit: Int,
    @SerialName("nextCursor") val nextCursor: String,
    @SerialName("hasMore") val hasMore: Boolean
)