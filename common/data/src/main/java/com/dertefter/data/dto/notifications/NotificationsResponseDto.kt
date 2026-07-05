package com.dertefter.data.dto.notifications

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationsResponseDto(
    @SerialName("notifications") val notifications: List<NotificationDto>,
    @SerialName("hasMore") val hasMore: Boolean
)