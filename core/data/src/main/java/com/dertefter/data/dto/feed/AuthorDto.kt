package com.dertefter.data.dto.feed

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthorDto(
    @SerialName("id") val id: String,
    @SerialName("pin") val pin: PinDto? = null, // Некоторые авторы не имеют значков (null)
    @SerialName("avatar") val avatar: String, // Смайлики/эмодзи приходят строкой
    @SerialName("username") val username: String,
    @SerialName("verified") val verified: Boolean,
    @SerialName("hasNuksta") val hasNuksta: Boolean,
    @SerialName("displayName") val displayName: String
)