package com.dertefter.data.dto.search

import kotlinx.serialization.Serializable

@Serializable
data class TopClansResponseDto(
    val clans: List<TopClanDto>
)