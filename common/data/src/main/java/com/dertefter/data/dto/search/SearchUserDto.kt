package com.dertefter.data.dto.search

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchUserDto(
    @SerialName("id") val id: String,
    @SerialName("username") val username: String,
    @SerialName("displayName") val displayName: String,
    @SerialName("avatar") val avatar: String,
    @SerialName("verified") val verified: Boolean,
    @SerialName("hasNuksta") val hasNuksta: Boolean,
    @SerialName("followersCount") val followersCount: Int
)
