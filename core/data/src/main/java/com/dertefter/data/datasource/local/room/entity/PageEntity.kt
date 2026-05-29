package com.dertefter.data.datasource.local.room.entity

import androidx.room.Entity

enum class PageType {
    POST,
    COMMENT,
    USER,
    NOTIFICATION
}

@Entity(
    tableName = "pages",
    primaryKeys = ["type", "tab", "self"]
)
data class PageEntity(
    val type: PageType,
    val tab: String,
    val self: String,
    val prev: String?,
    val next: String?,
    val isEmpty: Boolean
)
