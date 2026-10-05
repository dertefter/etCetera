package com.dertefter.data.dto.feed

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthorDto(
    @SerialName("id") val id: String,
    @SerialName("avatar") val avatar: String,
    @SerialName("pin") val pin: Pin? = null,
    @SerialName("username") val username: String,
    @SerialName("verified") val verified: Boolean,
    @SerialName("hasNuksta") val hasNuksta: Boolean,
    @SerialName("displayName") val displayName: String
)