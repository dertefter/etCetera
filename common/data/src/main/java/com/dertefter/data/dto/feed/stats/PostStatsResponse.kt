package com.dertefter.data.dto.feed.stats

import kotlinx.serialization.Serializable

@Serializable
data class PostStatsResponse(
    val posts: List<PostStatsDto>
)

