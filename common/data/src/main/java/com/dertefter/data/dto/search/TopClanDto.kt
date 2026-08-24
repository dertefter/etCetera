package com.dertefter.data.dto.search

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TopClanDto(
    @SerialName("avatar") val avatar: String,
    @SerialName("memberCount") val postsCount: Int
)