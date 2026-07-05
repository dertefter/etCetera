package com.dertefter.data.dto.feed.stats

import com.google.gson.annotations.SerializedName

data class PostStatsDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("likesCount")
    val likesCount: Int,

    @SerializedName("commentsCount")
    val commentsCount: Int,

    @SerializedName("repostsCount")
    val repostsCount: Int,

    @SerializedName("viewsCount")
    val viewsCount: Int,

    @SerializedName("dominantEmoji")
    val dominantEmoji: String
)