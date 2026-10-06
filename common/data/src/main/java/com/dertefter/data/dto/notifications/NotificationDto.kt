package com.dertefter.data.dto.notifications

import kotlinx.serialization.Serializable

@Serializable
data class NotificationDto(
    val id: String,
    val type: String,
    val targetType: String? = null,
    val targetId: String? = null,
    val subjectType: String? = null,
    val subjectId: String? = null,
    val preview: String? = null,
    val readAt: String? = null,
    val createdAt: String,
    val actor: ActorDto? = null,
    val read: Boolean,
    val count: Int = 1,
    val title: String? = null,
    val eventId: String? = null,
    val eventCycle: Int? = null,
    val expiresAt: String? = null,
    val link: String? = null,
)
