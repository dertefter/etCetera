package com.dertefter.data.dto.messenger

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatDto(
    @SerialName("chat_id") val chatId: String,
    @SerialName("chat_type") val chatType: Int? = 0,
    @SerialName("other_user_id") val otherUserId: String? = null,
    val title: String? = null,
    val avatar: String? = null,
    @SerialName("other_user_deleted") val otherUserDeleted: Boolean = false,
    @SerialName("last_message") val lastMessage: LastMessageDto? = null,
    @SerialName("other_read_message_id") val otherReadMessageId: String? = null,
    @SerialName("my_read_message_id") val myReadMessageId: String? = null,
    @SerialName("unread_count") val unreadCount: Int = 0,
    @SerialName("marked_unread") val markedUnread: Boolean = false,
    @SerialName("is_request") val isRequest: Boolean = false,
    @SerialName("is_pinned") val isPinned: Boolean = false,
    @SerialName("is_muted") val isMuted: Boolean = false
)

@Serializable
data class LastMessageDto(
    val id: String? = null,
    val text: String? = null,
    val at: String? = null,
    @SerialName("sender_id") val senderId: String? = null,
    @SerialName("attachment_kind") val attachmentKind: String? = null,
    @SerialName("attachment_count") val attachmentCount: Int = 0,
    @SerialName("attachment_filename") val attachmentFilename: String? = null
)

@Serializable
data class ChatsResponseDto(
    val chats: List<ChatDto> = emptyList(),
    @SerialName("event_seq") val eventSeq: Long? = null,
    @SerialName("next_before") val nextBefore: String? = null
)
