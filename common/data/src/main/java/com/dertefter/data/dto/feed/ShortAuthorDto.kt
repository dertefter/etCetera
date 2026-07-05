package com.dertefter.data.dto.feed

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShortAuthorDto(
    @SerialName("id") val id: String,
    @SerialName("username") val username: String,
    @SerialName("displayName") val displayName: String,
    @SerialName("avatar") val avatar: String,
    @SerialName("verified") val verified: Boolean,
    @SerialName("pin") val pin: PinDto? = null,
    @SerialName("hasNuksta") val hasNuksta: Boolean
)