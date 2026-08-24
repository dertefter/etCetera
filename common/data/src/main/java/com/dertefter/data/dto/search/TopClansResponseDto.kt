package com.dertefter.data.dto.search

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TopClansResponseDto(
    @SerialName("clans") val clans: List<TopClanDto>
)