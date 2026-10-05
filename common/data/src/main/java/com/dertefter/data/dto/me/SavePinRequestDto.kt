package com.dertefter.data.dto.me

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SavePinRequestDto(
    @SerialName("slug") val slug: String
)
