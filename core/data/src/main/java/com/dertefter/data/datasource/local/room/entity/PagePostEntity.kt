package com.dertefter.data.datasource.local.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "page_posts",
    primaryKeys = ["type", "tab", "self", "postId"],
    foreignKeys = [
        ForeignKey(
            entity = PageEntity::class,
            parentColumns = ["type", "tab", "self"],
            childColumns = ["type", "tab", "self"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PostEntity::class,
            parentColumns = ["id"],
            childColumns = ["postId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["postId"]),
        Index(value = ["type", "tab", "self"])
    ]
)
data class PagePostEntity(
    val type: PageType,
    val tab: String,
    val self: String,
    val postId: String,
    val orderIndex: Int
)
