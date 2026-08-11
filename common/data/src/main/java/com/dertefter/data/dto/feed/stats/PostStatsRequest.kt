package com.dertefter.data.dto.feed.stats

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostStatsRequest(
    @SerialName("ids")
    val ids: List<String>
)
