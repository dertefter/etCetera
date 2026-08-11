package com.dertefter.data.dto.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class VisibilityDto {
    @SerialName("everyone")
    EVERYONE,

    @SerialName("followers")
    FOLLOWERS, // подписчики

    @SerialName("mutual")
    MUTUAL, // взаимные подписчики

    @SerialName("nobody")
    NOBODY
}
