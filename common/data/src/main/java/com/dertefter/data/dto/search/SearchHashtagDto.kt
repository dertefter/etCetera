package com.dertefter.data.dto.search

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchHashtagDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("postsCount") val postsCount: Int
)