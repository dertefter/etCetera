package com.dertefter.data.dto.comments

import com.dertefter.data.dto.event.ActiveNicknameDto
import kotlinx.serialization.Serializable

@Serializable
data class ReplyToDto(
    val id: String,
    val username: String,
    val displayName: String,
    val activeNickname: ActiveNicknameDto? = null
)
