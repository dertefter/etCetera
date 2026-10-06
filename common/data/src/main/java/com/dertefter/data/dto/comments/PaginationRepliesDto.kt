package com.dertefter.data.dto.comments

import kotlinx.serialization.Serializable

@Serializable
data class PaginationRepliesDto(
    val limit: Int,
    val page: Int,
    val hasMore: Boolean,
    val total: Int?
)