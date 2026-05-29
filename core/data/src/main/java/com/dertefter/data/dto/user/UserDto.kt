package com.dertefter.data.dto.user

import com.dertefter.data.dto.feed.PinDto
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val avatar: String,
    val banner: String?,
    val bio: String?,
    val createdAt: String,
    val displayName: String,
    val followersCount: Int,
    val followingCount: Int,
    val hasNuksta: Boolean,
    val id: String,
    val isFollowedBy: Boolean,
    val isFollowing: Boolean,
    val lastSeen: LastSeenDto?,
    val likesVisibility: VisibilityDto,
    val online: Boolean,
    val pin: PinDto?,
    val pinnedPostId: String?,
    val postsCount: Int,
    val username: String,
    val verified: Boolean,
    val wallAccess: VisibilityDto
)

