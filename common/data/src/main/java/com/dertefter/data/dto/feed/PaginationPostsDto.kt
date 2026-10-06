package com.dertefter.data.dto.feed

import kotlinx.serialization.Serializable

@Serializable
data class PaginationPostsDto(
    val limit: Int,
    val nextCursor: String?,
    val hasMore: Boolean
)