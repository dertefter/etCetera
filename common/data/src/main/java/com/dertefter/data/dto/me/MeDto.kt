package com.dertefter.data.dto.me

import com.dertefter.data.dto.feed.PinDto
import com.dertefter.data.dto.user.SubscriptionDto
import com.dertefter.data.dto.user.VisibilityDto
import kotlinx.serialization.Serializable

@Serializable
data class MeDto(
    val avatar: String,
    val banner: String?,
    val bio: String?,
    val createdAt: String,
    val displayName: String,
    val followersCount: Int,
    val followingCount: Int,
    val id: String,
    val isPhoneVerified: Boolean,
    val isPrivate: Boolean,
    val likesVisibility: VisibilityDto,
    val pin: PinDto?,
    val postsCount: Int,
    val subscription: SubscriptionDto?,
    val username: String,
    val verified: Boolean,
    val wallAccess: VisibilityDto
)

