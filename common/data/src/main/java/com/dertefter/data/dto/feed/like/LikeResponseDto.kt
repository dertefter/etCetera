package com.dertefter.data.dto.feed.like

import com.google.gson.annotations.SerializedName

data class LikeResponseDto(
    @SerializedName("liked")
    val liked: Boolean,

    @SerializedName("likesCount")
    val likesCount: Int
)

