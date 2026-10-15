package com.dertefter.data.datasource.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dertefter.data.dto.draft.DraftDto
import com.dertefter.data.dto.feed.SpanDto
import com.dertefter.data.dto.new_post.NewPollDto

@Entity(tableName = "post_drafts")
data class PostDraftEntity(
    @PrimaryKey
    val draftKey: String,
    val content: String,
    val spans: List<SpanDto>,
    val poll: NewPollDto?,
    val attachmentIds: List<String>,
    val wallRecipientId: String?,
    val postId: String? = null,
    val commentId: String? = null,
    val replyToUserId: String? = null
)

fun PostDraftEntity.asExternalModel(): DraftDto {
    return DraftDto(
        content = content,
        spans = spans,
        poll = poll,
        attachmentIds = attachmentIds,
        wallRecipientId = wallRecipientId,
        postId = postId,
        commentId = commentId,
        replyToUserId = replyToUserId
    )
}

fun DraftDto.asEntity(key: String): PostDraftEntity {
    return PostDraftEntity(
        draftKey = key,
        content = content,
        spans = spans,
        poll = poll,
        attachmentIds = attachmentIds,
        wallRecipientId = wallRecipientId,
        postId = postId,
        commentId = commentId,
        replyToUserId = replyToUserId
    )
}
