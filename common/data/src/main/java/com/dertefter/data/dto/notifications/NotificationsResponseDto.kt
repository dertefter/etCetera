package com.dertefter.data.dto.notifications

import kotlinx.serialization.Serializable

@Serializable
data class NotificationsResponseDto(
    val notifications: List<NotificationDto>,
    val hasMore: Boolean
)