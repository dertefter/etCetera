package com.dertefter.data.dto.notifications

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationDto(
    @SerialName("id") val id: String,
    @SerialName("type") val type: String, // Можно заменить на Enum, если типы (like, comment, follow, wall_post) фиксированы
    @SerialName("targetType") val targetType: String?,
    @SerialName("targetId") val targetId: String?,
    @SerialName("subjectType") val subjectType: String? = null,
    @SerialName("subjectId") val subjectId: String? = null,
    @SerialName("preview") val preview: String?,
    @SerialName("readAt") val readAt: String?,
    @SerialName("createdAt") val createdAt: String,
    @SerialName("actor") val actor: ActorDto,
    @SerialName("read") val read: Boolean,
    @SerialName("count") val count: Int = 1
)