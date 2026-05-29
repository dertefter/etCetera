package com.dertefter.data.dto.search

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchDataDto(
    @SerialName("users") val users: List<SearchUserDto>,
    @SerialName("hashtags") val hashtags: List<SearchHashtagDto>
)