package com.dertefter.data.dto.comments

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaginationRepliesDto(
    @SerialName("limit") val limit: Int,
    @SerialName("page") val page: Int,
    @SerialName("hasMore") val hasMore: Boolean,
    @SerialName("total") val total: Int?
)