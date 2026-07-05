package com.dertefter.data.datasource.local.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "page_comments",
    primaryKeys = ["type", "tab", "self", "commentId"],
    foreignKeys = [
        ForeignKey(
            entity = PageEntity::class,
            parentColumns = ["type", "tab", "self"],
            childColumns = ["type", "tab", "self"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CommentEntity::class,
            parentColumns = ["id"],
            childColumns = ["commentId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["commentId"]),
        Index(value = ["type", "tab", "self"])
    ]
)
data class PageCommentEntity(
    val type: PageType,
    val tab: String,
    val self: String,
    val commentId: String,
    val orderIndex: Int
)
