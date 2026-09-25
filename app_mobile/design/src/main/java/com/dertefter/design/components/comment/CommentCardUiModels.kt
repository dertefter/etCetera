package com.dertefter.design.components.comment

import com.dertefter.design.common.DateParser
import com.dertefter.design.components.post.AttachmentUiModel
import com.dertefter.design.components.post.AuthorUiModel
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

data class CommentUiModel(
    val id: String,
    val content: String,
    val author: AuthorUiModel,
    val likesCount: Int,
    val repliesCount: Int?,
    val isLiked: Boolean,
    val createdAt: String,
    val replyTo: ReplyToUiModel? = null,
    val attachments: List<AttachmentUiModel> = emptyList(),
    val replies: List<CommentUiModel>? = emptyList(),
    val isOwner: Boolean = false
){
    fun getCreatedAtDate(): LocalDateTime? {
        return DateParser.parseToInstant(createdAt)
            ?.atZone(ZoneId.systemDefault())
            ?.toLocalDateTime()
    }
    fun canEdit(): Boolean {
        if (!isOwner) return false
        val created = DateParser.parseToInstant(createdAt) ?: return false
        return Instant.now().isBefore(created.plus(Duration.ofHours(48)))
    }

}

data class ReplyToUiModel(
    val id: String,
    val username: String,
    val displayName: String
)
