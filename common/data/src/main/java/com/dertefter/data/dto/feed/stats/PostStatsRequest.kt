package com.dertefter.data.dto.feed.stats

import kotlinx.serialization.Serializable

@Serializable
data class PostStatsRequest(
    val ids: List<String>
)
