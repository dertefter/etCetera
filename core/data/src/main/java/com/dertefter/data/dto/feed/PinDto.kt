package com.dertefter.data.dto.feed

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PinDto(
    @SerialName("slug") val slug: String,
    @SerialName("name") val name: String,
    @SerialName("description") val description: String,
    @SerialName("url") val url: String
)