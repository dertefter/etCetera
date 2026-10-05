package com.dertefter.data.dto.me

import com.dertefter.data.dto.feed.Pin
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PinsDataDto(
    @SerialName("pins") val pins: List<Pin>,
    @SerialName("activePin") val activePin: String?
)
