package com.dertefter.data.dto.feed.stats

import com.google.gson.annotations.SerializedName

data class PostStatsRequest(
    @SerializedName("ids")
    val ids: List<String>
)