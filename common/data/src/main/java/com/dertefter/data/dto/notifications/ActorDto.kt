package com.dertefter.data.dto.notifications

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ActorDto(
    @SerialName("id") val id: String,
    @SerialName("displayName") val displayName: String,
    @SerialName("username") val username: String,
    @SerialName("avatar") val avatar: String, // Содержит эмодзи или URL
    @SerialName("isFollowing") val isFollowing: Boolean,
    @SerialName("isFollowedBy") val isFollowedBy: Boolean
)