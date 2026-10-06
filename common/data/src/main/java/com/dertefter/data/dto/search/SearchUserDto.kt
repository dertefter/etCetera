package com.dertefter.data.dto.search

import com.dertefter.data.dto.event.ActiveNicknameDto
import kotlinx.serialization.Serializable

@Serializable
data class SearchUserDto(
    val id: String,
    val username: String,
    val displayName: String,
    val avatar: String,
    val verified: Boolean,
    val hasNuksta: Boolean,
    val followersCount: Int,
    val activeNickname: ActiveNicknameDto? = null
)
