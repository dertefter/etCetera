package com.dertefter.data.dto.user

import com.dertefter.data.dto.common.ActiveNicknameDto
import com.dertefter.data.dto.feed.Pin
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val avatar: String = "",
    val clanAvatar: String = "",
    val banner: String? = null,
    val bio: String? = null,
    val createdAt: String = "",
    val displayName: String = "",
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val hasNuksta: Boolean = false,
    val id: String,
    val isFollowedBy: Boolean = false,
    val isFollowing: Boolean = false,
    val lastSeen: LastSeenDto? = null,
    val likesVisibility: VisibilityDto = VisibilityDto.NOBODY,
    val isBlockedByMe: Boolean = false,
    val online: Boolean = false,
    val pin: Pin? = null,
    val pinnedPostId: String? = null,
    val postsCount: Int = 0,
    val username: String = "",
    val verified: Boolean = false,
    val wallAccess: VisibilityDto = VisibilityDto.EVERYONE,
    val isPrivate: Boolean = false,
    val canMessage: Boolean = true,
    val activeNickname: ActiveNicknameDto? = null
)
