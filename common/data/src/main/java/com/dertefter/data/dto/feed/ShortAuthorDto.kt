package com.dertefter.data.dto.feed

import com.dertefter.data.dto.event.ActiveNicknameDto
import kotlinx.serialization.Serializable

@Serializable
data class ShortAuthorDto(
    val id: String,
    val username: String,
    val displayName: String,
    val avatar: String,
    val verified: Boolean,
    val pin: Pin? = null,
    val hasNuksta: Boolean,
    val activeNickname: ActiveNicknameDto? = null
)

fun ShortAuthorDto.asAuthorDto() = AuthorDto(
    id = id,
    avatar = avatar,
    pin = pin,
    username = username,
    verified = verified,
    hasNuksta = hasNuksta,
    displayName = displayName,
    activeNickname = activeNickname
)
