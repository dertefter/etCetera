package com.dertefter.data.dto.feed.stats

import kotlinx.serialization.Serializable

@Serializable
data class PostStatsDto(
    val id: String,
    val likesCount: Int,
    val commentsCount: Int,
    val repostsCount: Int,
    val viewsCount: Int,
    val dominantEmoji: String?
)
