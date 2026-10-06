package com.dertefter.data.datasource.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dertefter.data.dto.event.ActiveNicknameDto
import com.dertefter.data.dto.feed.Pin
import com.dertefter.data.dto.followers.FollowerUserDto
import com.dertefter.data.dto.me.Me
import com.dertefter.data.dto.me.Privacy
import com.dertefter.data.dto.user.LastSeenDto
import com.dertefter.data.dto.user.SubscriptionDto
import com.dertefter.data.dto.user.UserDto
import com.dertefter.data.dto.user.VisibilityDto

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val avatar: String,
    val clanAvatar: String,
    val banner: String?,
    val bio: String?,
    val createdAt: String,
    val displayName: String,
    val followersCount: Int,
    val followingCount: Int,
    val hasNuksta: Boolean,
    val isFollowedBy: Boolean,
    val isFollowing: Boolean,
    val lastSeen: LastSeenDto?,
    val likesVisibility: VisibilityDto,
    val online: Boolean,
    val pin: Pin?,
    val pinnedPostId: String?,
    val postsCount: Int,
    val username: String,
    val verified: Boolean,
    val wallAccess: VisibilityDto,
    val isBlockedByMe: Boolean = false,

    // Me fields
    val isPhoneVerified: Boolean = false,
    val isPrivate: Boolean = false,
    val canMessage: Boolean = true,
    val messageAccess: VisibilityDto = VisibilityDto.EVERYONE,
    val showLastSeen: Boolean = true,
    val subscription: SubscriptionDto? = null,
    val isMe: Boolean = false,
    val activeNickname: ActiveNicknameDto? = null
)

fun UserEntity.asExternalModel() = UserDto(
    clanAvatar = clanAvatar,
    avatar = avatar,
    banner = banner,
    bio = bio,
    createdAt = createdAt,
    displayName = displayName,
    followersCount = followersCount,
    followingCount = followingCount,
    hasNuksta = hasNuksta,
    id = id,
    isFollowedBy = isFollowedBy,
    isFollowing = isFollowing,
    lastSeen = lastSeen,
    likesVisibility = likesVisibility,
    online = online,
    pin = pin,
    pinnedPostId = pinnedPostId,
    postsCount = postsCount,
    username = username,
    verified = verified,
    wallAccess = wallAccess,
    isBlockedByMe = isBlockedByMe,
    isPrivate = isPrivate,
    canMessage = canMessage,
    activeNickname = activeNickname
)

fun UserEntity.asFollowerExternalModel() = FollowerUserDto(
    id = id,
    username = username,
    displayName = displayName,
    avatar = avatar,
    verified = verified,
    isFollowing = isFollowing
)

fun FollowerUserDto.asEntity() = UserEntity(
    id = id,
    username = username,
    displayName = displayName,
    avatar = avatar,
    verified = verified,
    isFollowing = isFollowing,

    // Default values for other fields
    clanAvatar = avatar,
    banner = null,
    bio = null,
    createdAt = "",
    followersCount = 0,
    followingCount = 0,
    hasNuksta = false,
    isFollowedBy = false,
    lastSeen = null,
    likesVisibility = VisibilityDto.EVERYONE,
    online = false,
    pin = null,
    pinnedPostId = null,
    postsCount = 0,
    wallAccess = VisibilityDto.EVERYONE,
    isMe = false
)

fun UserEntity.asPrivacyDto() = Privacy(
    isPrivate = isPrivate,
    wallAccess = wallAccess,
    likesVisibility = likesVisibility,
    messageAccess = messageAccess,
    showLastSeen = showLastSeen
)

fun UserEntity.asMeExternalModel() = Me(
    clanAvatar = clanAvatar,
    avatar = avatar,
    banner = banner,
    bio = bio,
    createdAt = createdAt,
    displayName = displayName,
    followersCount = followersCount,
    followingCount = followingCount,
    id = id,
    isPhoneVerified = isPhoneVerified,
    isPrivate = isPrivate,
    likesVisibility = likesVisibility,
    pin = pin,
    postsCount = postsCount,
    subscription = subscription,
    username = username,
    verified = verified,
    wallAccess = wallAccess,
    activeNickname = activeNickname
)

fun UserDto.asEntity() = UserEntity(
    clanAvatar = clanAvatar,
    avatar = avatar,
    banner = banner,
    bio = bio,
    createdAt = createdAt,
    displayName = displayName,
    followersCount = followersCount,
    followingCount = followingCount,
    hasNuksta = hasNuksta,
    id = id,
    isFollowedBy = isFollowedBy,
    isFollowing = isFollowing,
    lastSeen = lastSeen,
    likesVisibility = likesVisibility,
    online = online,
    pin = pin,
    pinnedPostId = pinnedPostId,
    postsCount = postsCount,
    username = username,
    verified = verified,
    wallAccess = wallAccess,
    isBlockedByMe = isBlockedByMe,
    isMe = false,
    isPrivate = isPrivate,
    canMessage = canMessage,
    activeNickname = activeNickname
)

fun Me.asEntity() = UserEntity(
    clanAvatar = clanAvatar,
    avatar = avatar,
    banner = banner,
    bio = bio,
    createdAt = createdAt,
    displayName = displayName,
    followersCount = followersCount,
    followingCount = followingCount,
    id = id,
    isPhoneVerified = isPhoneVerified,
    isPrivate = isPrivate,
    likesVisibility = likesVisibility,
    pin = pin,
    postsCount = postsCount,
    subscription = subscription,
    username = username,
    verified = verified,
    wallAccess = wallAccess,
    isMe = true,

    // User fields defaults
    hasNuksta = false,
    isFollowedBy = false,
    isFollowing = false,
    lastSeen = null,
    online = true,
    pinnedPostId = null,
    activeNickname = activeNickname
)
