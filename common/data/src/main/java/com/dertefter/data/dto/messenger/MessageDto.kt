package com.dertefter.data.dto.messenger

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageDto(
    @SerialName("message_id") val messageId: String,
    @SerialName("sender_id") val senderId: String? = null,
    val text: String? = null,
    val attachments: List<MessageAttachmentDto> = emptyList(),
    @SerialName("reply_to") val replyTo: String? = null,
    @SerialName("forwarded_from_msg") val forwardedFromMsg: String? = null,
    @SerialName("forwarded_from_chat") val forwardedFromChat: String? = null,
    @SerialName("forwarded_from_sender_id") val forwardedFromSenderId: String? = null,
    @SerialName("forwarded_from_name") val forwardedFromName: String? = null,
    @SerialName("edited_at") val editedAt: String? = null,
    @SerialName("is_deleted") val isDeleted: Boolean = false,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("reaction_version") val reactionVersion: String? = null
)

@Serializable
data class MessageAttachmentDto(
    val fileId: String? = null,
    val url: String? = null,
    val expiresIn: Long? = null,
    val filename: String? = null,
    val mime: String? = null,
    val sizePlain: Long? = null,
    val width: Int? = null,
    val height: Int? = null,
    val durationMs: Long? = null,
    val thumbhash: String? = null,
    val waveform: String? = null,
    val thumbUrl: String? = null,
    val thumbWidth: Int? = null,
    val thumbHeight: Int? = null
)

@Serializable
data class ReadCursorDto(
    @SerialName("message_id") val messageId: String? = null,
    val timestamp: String? = null,
    @SerialName("read_at") val readAt: String? = null
)

@Serializable
data class MessagesResponseDto(
    val messages: List<MessageDto> = emptyList(),
    @SerialName("read_cursors") val readCursors: Map<String, ReadCursorDto>? = null,
    @SerialName("my_last_read") val myLastRead: ReadCursorDto? = null
)

@Serializable
data class FileLinksRequestDto(
    @SerialName("file_ids") val fileIds: List<String>
)

@Serializable
data class FileLinksResponseDto(
    val links: List<FileLinkDto> = emptyList()
)

@Serializable
data class FileLinkDto(
    val fileId: String,
    val url: String? = null,
    val expiresIn: Long? = null,
    val thumbUrl: String? = null
)
