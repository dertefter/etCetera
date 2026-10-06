package com.dertefter.data.dto.me

import kotlinx.serialization.Serializable

@Serializable
data class SavePinRequestDto(
    val slug: String
)
