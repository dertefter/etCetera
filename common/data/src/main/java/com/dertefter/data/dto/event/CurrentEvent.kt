package com.dertefter.data.dto.event

import kotlinx.serialization.Serializable

@Serializable
data class CurrentEvent(
    val active: Boolean = false,
    val title: String = "",
    val url: String = ""
)
