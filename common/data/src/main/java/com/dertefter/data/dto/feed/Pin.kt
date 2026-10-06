package com.dertefter.data.dto.feed

import kotlinx.serialization.Serializable

@Serializable
data class Pin(
    val slug: String,
    val name: String,
    val description: String,
    val url: String,
    val grantedAt: String? = null
)