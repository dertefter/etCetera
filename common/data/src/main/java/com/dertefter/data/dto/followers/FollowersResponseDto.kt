package com.dertefter.data.dto.followers

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FollowersResponseDto(
    @SerialName("data") val data: FollowersResponseDataDto
)

@Serializable
data class FollowersResponseDataDto(
    @SerialName("users") val users: List<FollowerUserDto>,
    @SerialName("pagination") val pagination: FollowersResponsePaginationDto
)

@Serializable
data class FollowerUserDto(
    @SerialName("id") val id: String,
    @SerialName("username") val username: String,
    @SerialName("displayName") val displayName: String,
    @SerialName("avatar") val avatar: String,
    @SerialName("verified") val verified: Boolean,
    @SerialName("isFollowing") val isFollowing: Boolean
)

@Serializable
data class FollowersResponsePaginationDto(
    @SerialName("page") val page: Int,
    @SerialName("limit") val limit: Int,
    @SerialName("total") val total: Int,
    @SerialName("hasMore") val hasMore: Boolean
)