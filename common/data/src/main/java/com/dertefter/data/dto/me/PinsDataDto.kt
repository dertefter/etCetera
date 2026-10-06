package com.dertefter.data.dto.me

import com.dertefter.data.dto.feed.Pin
import kotlinx.serialization.Serializable

@Serializable
data class PinsDataDto(
    val pins: List<Pin>,
    val activePin: String? = null
)
