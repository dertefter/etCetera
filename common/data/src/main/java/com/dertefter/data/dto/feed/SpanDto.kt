package com.dertefter.data.dto.feed

import kotlinx.serialization.Serializable

@Serializable
data class SpanDto(
    val type: String, // "mention", "hashtag", "underline" и т.д.
    val length: Int,
    val offset: Int,
    val username: String? = null,
    val tag: String? = null,
    val url: String? = null
)