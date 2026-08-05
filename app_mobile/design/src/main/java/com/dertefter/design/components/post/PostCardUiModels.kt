package com.dertefter.design.components.post

import com.dertefter.design.components.poll.PollUiModel
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

data class PostUiModel(
    val id: String,
    val content: String,
    val spans: List<SpanUiModel>,
    val author: AuthorUiModel,
    val attachments: List<AttachmentUiModel>,
    val poll: PollUiModel?,
    val likesCount: Int,
    val isLiked: Boolean,
    val commentsCount: Int,
    val repostsCount: Int,
    val isReposted: Boolean,
    val viewsCount: Int,
    val dominantEmoji: String?,
    val isPinned: Boolean,
    val isOwner: Boolean,
    val createdAt: String, // 2026-07-06T16:08:36.402Z
    val editedAt: String?,
    val originalPost: OriginalPostUiModel?
) {
    fun getCreatedAtDate(): LocalDate? {
        return try {
            Instant.parse(createdAt)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        } catch (_: Exception) {
            null
        }
    }

    fun canEdit(): Boolean {
        if (!isOwner) return false

        return try {
            val created = Instant.parse(createdAt)
            Instant.now().isBefore(created.plus(Duration.ofHours(48)))
        } catch (_: Exception) {
            false
        }
    }

    fun getEditedAtDate(): LocalDate? {
        return try {
            Instant.parse(editedAt)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        } catch (_: Exception) {
            null
        }
    }
}

data class SpanUiModel(
    val type: String,
    val length: Int,
    val offset: Int,
    val username: String? = null,
    val tag: String? = null,
    val url: String? = null
)

data class AuthorUiModel(
    val id: String,
    val username: String,
    val displayName: String,
    val avatar: String,
    val hasNuksta: Boolean,
    val verified: Boolean,
    val pin: PinUiModel?
)

data class AttachmentUiModel(
    val id: String,
    val type: String, // "image", "video"
    val url: String?,
    val mimeType: String? = null
)

data class PinUiModel(
    val description: String,
    val name: String,
    val slug: String?,
    val url: String?
)

data class OriginalPostUiModel(
    val id: String,
    val content: String,
    val spans: List<SpanUiModel>,
    val author: AuthorUiModel,
    val attachments: List<AttachmentUiModel>,
    val poll: PollUiModel?,
    val createdAt: String,
    val editedAt: String?,
    val isDeleted: Boolean
)
