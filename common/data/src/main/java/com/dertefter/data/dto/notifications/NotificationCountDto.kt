package com.dertefter.data.dto.notifications

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationCountDto(
    @SerialName("count") val count: Int
)
