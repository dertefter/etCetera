package com.dertefter.data.datasource.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dertefter.data.dto.notifications.ActorDto
import com.dertefter.data.dto.notifications.NotificationDto

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val type: String,
    val targetType: String?,
    val targetId: String?,
    val subjectType: String? = null,
    val subjectId: String? = null,
    val preview: String?,
    val readAt: String?,
    val createdAt: String,
    val actor: ActorDto?,
    val read: Boolean,
    val count: Int = 1,
    val title: String? = null,
    val eventId: String? = null,
    val eventCycle: Int? = null,
    val expiresAt: String? = null,
    val link: String? = null,
)

fun NotificationEntity.asExternalModel() = NotificationDto(
    id = id,
    type = type,
    targetType = targetType,
    targetId = targetId,
    subjectType = subjectType,
    subjectId = subjectId,
    preview = preview,
    readAt = readAt,
    createdAt = createdAt,
    actor = actor,
    read = read,
    count = count,
    title = title,
    eventId = eventId,
    eventCycle = eventCycle,
    expiresAt = expiresAt,
    link = link
)

fun NotificationDto.asEntity() = NotificationEntity(
    id = id,
    type = type,
    targetType = targetType,
    targetId = targetId,
    subjectType = subjectType,
    subjectId = subjectId,
    preview = preview,
    readAt = readAt,
    createdAt = createdAt,
    actor = actor,
    read = read,
    count = count,
    title = title,
    eventId = eventId,
    eventCycle = eventCycle,
    expiresAt = expiresAt,
    link = link
)
