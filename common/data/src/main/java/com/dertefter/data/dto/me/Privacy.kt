package com.dertefter.data.dto.me

import com.dertefter.data.dto.user.VisibilityDto
import kotlinx.serialization.Serializable

@Serializable
data class Privacy(
    val isPrivate: Boolean = false,
    val wallAccess: VisibilityDto = VisibilityDto.EVERYONE,
    val likesVisibility: VisibilityDto = VisibilityDto.EVERYONE,
    val messageAccess: VisibilityDto = VisibilityDto.EVERYONE,
    val showLastSeen: Boolean = true
)
