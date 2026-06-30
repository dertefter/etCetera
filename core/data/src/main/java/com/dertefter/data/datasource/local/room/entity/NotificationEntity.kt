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
    val preview: String?,
    val readAt: String?,
    val createdAt: String,
    val actor: ActorDto,
    val read: Boolean
)

fun NotificationEntity.asExternalModel() = NotificationDto(
    id = id,
    type = type,
    targetType = targetType,
    targetId = targetId,
    preview = preview,
    readAt = readAt,
    createdAt = createdAt,
    actor = actor,
    read = read
)

fun NotificationDto.asEntity() = NotificationEntity(
    id = id,
    type = type,
    targetType = targetType,
    targetId = targetId,
    preview = preview,
    readAt = readAt,
    createdAt = createdAt,
    actor = actor,
    read = read
)
