package com.dertefter.data.datasource.local.room.entity

import androidx.room.Entity

@Entity(
    tableName = "page_users",
    primaryKeys = ["type", "tab", "self", "userId"]
)
data class PageUserEntity(
    val type: PageType,
    val tab: String,
    val self: String,
    val userId: String,
    val orderIndex: Int
)
