package com.dertefter.data.dto.notifications

import com.dertefter.data.dto.event.ActiveNicknameDto
import kotlinx.serialization.Serializable

@Serializable
data class ActorDto(
    val id: String,
    val displayName: String,
    val username: String,
    val avatar: String,
    val isFollowing: Boolean,
    val isFollowedBy: Boolean,
    val activeNickname: ActiveNicknameDto? = null
)
