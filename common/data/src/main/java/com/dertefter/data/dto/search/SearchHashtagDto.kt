package com.dertefter.data.dto.search

import kotlinx.serialization.Serializable

@Serializable
data class SearchHashtagDto(
    val id: String,
    val name: String,
    val postsCount: Int
)