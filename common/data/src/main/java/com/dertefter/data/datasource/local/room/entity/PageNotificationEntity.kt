package com.dertefter.data.datasource.local.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "page_notifications",
    primaryKeys = ["type", "tab", "self", "notificationId"],
    foreignKeys = [
        ForeignKey(
            entity = PageEntity::class,
            parentColumns = ["type", "tab", "self"],
            childColumns = ["type", "tab", "self"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = NotificationEntity::class,
            parentColumns = ["id"],
            childColumns = ["notificationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["notificationId"]),
        Index(value = ["type", "tab", "self"])
    ]
)
data class PageNotificationEntity(
    val type: PageType,
    val tab: String,
    val self: String,
    val notificationId: String,
    val orderIndex: Int
)
