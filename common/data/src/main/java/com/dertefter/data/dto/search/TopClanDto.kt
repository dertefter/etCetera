package com.dertefter.data.dto.search

import kotlinx.serialization.Serializable

@Serializable
data class TopClanDto(
    val avatar: String,
    val postsCount: Int
)