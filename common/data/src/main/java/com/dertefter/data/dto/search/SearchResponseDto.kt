package com.dertefter.data.dto.search

import kotlinx.serialization.Serializable

@Serializable
data class SearchResponseDto(
    val data: SearchDataDto
)