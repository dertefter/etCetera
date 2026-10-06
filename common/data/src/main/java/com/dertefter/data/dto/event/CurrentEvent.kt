package com.dertefter.data.dto.event

import kotlinx.serialization.Serializable

@Serializable
data class CurrentEvent(
    val active: Boolean,
    val title: String,
    val url: String
)
