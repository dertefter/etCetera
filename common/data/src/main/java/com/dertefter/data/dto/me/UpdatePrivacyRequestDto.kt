package com.dertefter.data.dto.me

import com.dertefter.data.dto.user.VisibilityDto
import kotlinx.serialization.Serializable

@Serializable
data class UpdatePrivacyRequestDto(
    val wallAccess: VisibilityDto? = null,
    val likesVisibility: VisibilityDto? = null,
    val showLastSeen: Boolean? = null,
    val isPrivate: Boolean? = null,
    val messageAccess: VisibilityDto? = null
)
