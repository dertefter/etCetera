package com.dertefter.data.dto.event

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CurrentEvent(
    @SerialName("active") val active: Boolean,
    @SerialName("title") val title: String,
    @SerialName("url") val url: String
)
