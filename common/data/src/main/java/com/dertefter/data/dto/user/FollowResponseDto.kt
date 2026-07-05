package com.dertefter.data.dto.user

import kotlinx.serialization.Serializable

@Serializable
data class FollowResponseDto(
    val followersCount: Int = 0,
    val following: Boolean = false
)
