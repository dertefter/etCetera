package com.dertefter.data.dto.me

import com.dertefter.data.dto.event.ActiveNicknameDto
import com.dertefter.data.dto.feed.Pin
import com.dertefter.data.dto.user.SubscriptionDto
import com.dertefter.data.dto.user.VisibilityDto
import kotlinx.serialization.Serializable

@Serializable
data class Me(
    val avatar: String = "",
    val clanAvatar: String = avatar,
    val banner: String? = null,
    val bio: String? = null,
    val createdAt: String = "",
    val displayName: String = "",
    val followersCount: Int? = null,
    val followingCount: Int? = null,
    val id: String = "",
    val isPrivate: Boolean = false,
    val likesVisibility: VisibilityDto = VisibilityDto.EVERYONE,
    val pin: Pin? = null,
    val postsCount: Int = 0,
    val subscription: SubscriptionDto? = null,
    val username: String = "",
    val verified: Boolean = false,
    val wallAccess: VisibilityDto = VisibilityDto.EVERYONE,
    val activeNickname: ActiveNicknameDto? = null
)
