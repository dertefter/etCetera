package com.dertefter.data.dto.feed.stats

import com.google.gson.annotations.SerializedName

data class PostStatsResponse(
    @SerializedName("posts")
    val postStats: List<PostStatsDto>
)

