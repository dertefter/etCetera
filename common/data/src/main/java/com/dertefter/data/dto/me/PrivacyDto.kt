package com.dertefter.data.dto.me

import com.dertefter.data.dto.user.VisibilityDto
import kotlinx.serialization.Serializable

@Serializable
data class PrivacyDto(
    val isPrivate: Boolean,
    val wallAccess: VisibilityDto,
    val likesVisibility: VisibilityDto,
    val messageAccess: VisibilityDto,
    val showLastSeen: Boolean
)
