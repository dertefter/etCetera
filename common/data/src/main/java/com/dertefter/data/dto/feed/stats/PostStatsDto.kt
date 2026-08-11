package com.dertefter.data.dto.feed.stats

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostStatsDto(
    @SerialName("id")
    val id: String,

    @SerialName("likesCount")
    val likesCount: Int,

    @SerialName("commentsCount")
    val commentsCount: Int,

    @SerialName("repostsCount")
    val repostsCount: Int,

    @SerialName("viewsCount")
    val viewsCount: Int,

    @SerialName("dominantEmoji")
    val dominantEmoji: String?
)
