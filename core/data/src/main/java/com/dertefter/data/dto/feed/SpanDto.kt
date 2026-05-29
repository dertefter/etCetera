package com.dertefter.data.dto.feed

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SpanDto(
    @SerialName("type") val type: String, // "mention", "hashtag", "underline" и т.д.
    @SerialName("length") val length: Int,
    @SerialName("offset") val offset: Int,
    @SerialName("username") val username: String? = null,
    @SerialName("tag") val tag: String? = null
)