package com.dertefter.data.dto.notifications

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationDto(
    @SerialName("id") val id: String,
    @SerialName("type") val type: String,
    @SerialName("targetType") val targetType: String? = null,
    @SerialName("targetId") val targetId: String? = null,
    @SerialName("subjectType") val subjectType: String? = null,
    @SerialName("subjectId") val subjectId: String? = null,
    @SerialName("preview") val preview: String? = null,
    @SerialName("readAt") val readAt: String? = null,
    @SerialName("createdAt") val createdAt: String,
    @SerialName("actor") val actor: ActorDto? = null,
    @SerialName("read") val read: Boolean,
    @SerialName("count") val count: Int = 1,
    @SerialName("title") val title: String? = null,
    @SerialName("eventId") val eventId: String? = null,
    @SerialName("eventCycle") val eventCycle: Int? = null,
    @SerialName("expiresAt") val expiresAt: String? = null,
    @SerialName("link") val link: String? = null,
)
