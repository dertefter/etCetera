package com.dertefter.data.dto.search

import kotlinx.serialization.Serializable

@Serializable
data class SearchDataDto(
    val users: List<SearchUserDto> = emptyList(),
    val hashtags: List<SearchHashtagDto> = emptyList()
)