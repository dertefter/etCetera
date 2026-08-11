package com.dertefter.data.dto.feed.stats

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostStatsResponse(
    @SerialName("posts")
    val postStats: List<PostStatsDto>
)

