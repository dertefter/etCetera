package com.dertefter.data.dto.comments

import com.dertefter.data.dto.feed.AttachmentDto
import com.dertefter.data.dto.feed.AuthorDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentDto(
    @SerialName("id") val id: String,
    @SerialName("content") val content: String,
    @SerialName("author") val author: AuthorDto,
    @SerialName("likesCount") val likesCount: Int,
    @SerialName("repliesCount") val repliesCount: Int? = 0,
    @SerialName("isLiked") val isLiked: Boolean,
    @SerialName("createdAt") val createdAt: String, // Можно кастомный сериализатор для даты, если нужен Instant/LocalDateTime
    @SerialName("attachments") val attachments: List<AttachmentDto> = emptyList(),
    @SerialName("replies") val replies: List<CommentDto>? = emptyList(), // Рекурсивная вложенность ответов
    @SerialName("replyTo") val replyTo: ReplyToDto? = null
)