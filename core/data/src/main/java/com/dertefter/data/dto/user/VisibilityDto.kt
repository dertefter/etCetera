package com.dertefter.data.dto.user

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class VisibilityDto {
    @SerializedName("everyone")
    @SerialName("everyone")
    EVERYONE,

    @SerializedName("followers")
    @SerialName("followers")
    FOLLOWERS, // подписчики

    @SerializedName("mutual")
    @SerialName("mutual")
    MUTUAL, // взаимные подписчики

    @SerializedName("nobody")
    @SerialName("nobody")
    NOBODY
}
