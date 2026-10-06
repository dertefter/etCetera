package com.dertefter.data.dto.followers

import com.dertefter.data.dto.event.ActiveNicknameDto
import kotlinx.serialization.Serializable

@Serializable
data class FollowersResponseDto(
    val data: FollowersResponseDataDto
)

@Serializable
data class FollowersResponseDataDto(
    val users: List<FollowerUserDto>,
    val pagination: FollowersResponsePaginationDto
)

@Serializable
data class FollowerUserDto(
    val id: String,
    val username: String,
    val displayName: String,
    val avatar: String,
    val verified: Boolean,
    val isFollowing: Boolean,
    val activeNickname: ActiveNicknameDto? = null
)

@Serializable
data class FollowersResponsePaginationDto(
    val page: Int,
    val limit: Int,
    val total: Int,
    val hasMore: Boolean
)
