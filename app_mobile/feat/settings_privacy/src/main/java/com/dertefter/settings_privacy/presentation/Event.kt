package com.dertefter.settings_privacy.presentation

import com.dertefter.data.dto.user.VisibilityDto

sealed interface Event {

    data object OnNavigateBack : Event
    data object OnRefresh : Event

    data class ChangeIsPrivate(val value: Boolean) : Event
    data class ChangeWallAccess(val value: VisibilityDto) : Event
    data class ChangeLikesVisibility(val value: VisibilityDto) : Event
    data class ChangeMessageAccess(val value: VisibilityDto) : Event
    data class ChangeShowLastSeen(val value: Boolean) : Event
}
