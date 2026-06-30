package com.dertefter.data.datasource.local.room.converters

import androidx.room.TypeConverter
import com.dertefter.data.dto.comments.ReplyToDto
import com.dertefter.data.dto.feed.AttachmentDto
import com.dertefter.data.dto.feed.AuthorDto
import com.dertefter.data.dto.feed.OriginalPostDto
import com.dertefter.data.dto.feed.PinDto
import com.dertefter.data.dto.feed.PollDto
import com.dertefter.data.dto.feed.SpanDto
import com.dertefter.data.dto.notifications.ActorDto
import com.dertefter.data.dto.user.LastSeenDto
import com.dertefter.data.dto.user.SubscriptionDto
import com.dertefter.data.dto.user.VisibilityDto
import kotlinx.serialization.json.Json

class RoomConverters {
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @TypeConverter
    fun fromSubscription(subscriptionDto: SubscriptionDto?): String? {
        return subscriptionDto?.let { json.encodeToString(it) }
    }

    @TypeConverter
    fun toSubscription(subscriptionString: String?): SubscriptionDto? {
        return subscriptionString?.let { json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromPinDto(pinDto: PinDto?): String? {
        return pinDto?.let { json.encodeToString(it) }
    }

    @TypeConverter
    fun toPinDto(pinDtoString: String?): PinDto? {
        return pinDtoString?.let { json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromLastSeenDto(lastSeenDto: LastSeenDto?): String? {
        return lastSeenDto?.let { json.encodeToString(it) }
    }

    @TypeConverter
    fun toLastSeenDto(lastSeenDtoString: String?): LastSeenDto? {
        return lastSeenDtoString?.let { json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromVisibilityDto(visibilityDto: VisibilityDto?): String? {
        return visibilityDto?.name?.lowercase()
    }

    @TypeConverter
    fun toVisibilityDto(value: String?): VisibilityDto {
        return value?.let {
            try {
                VisibilityDto.valueOf(it.uppercase())
            } catch (_: Exception) {
                VisibilityDto.EVERYONE
            }
        } ?: VisibilityDto.EVERYONE
    }

    @TypeConverter
    fun fromSpanList(spans: List<SpanDto>): String {
        return json.encodeToString(spans)
    }

    @TypeConverter
    fun toSpanList(spansString: String): List<SpanDto> {
        return json.decodeFromString(spansString)
    }

    @TypeConverter
    fun fromAuthorDto(author: AuthorDto): String {
        return json.encodeToString(author)
    }

    @TypeConverter
    fun toAuthorDto(authorString: String): AuthorDto {
        return json.decodeFromString(authorString)
    }

    @TypeConverter
    fun fromAttachmentList(attachments: List<AttachmentDto>): String {
        return json.encodeToString(attachments)
    }

    @TypeConverter
    fun toAttachmentList(attachmentsString: String): List<AttachmentDto> {
        return json.decodeFromString(attachmentsString)
    }

    @TypeConverter
    fun fromOriginalPostDto(originalPost: OriginalPostDto?): String? {
        return originalPost?.let { json.encodeToString(it) }
    }

    @TypeConverter
    fun toOriginalPostDto(originalPostString: String?): OriginalPostDto? {
        return originalPostString?.let { json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromPollDto(poll: PollDto?): String? {
        return poll?.let { json.encodeToString(it) }
    }

    @TypeConverter
    fun toPollDto(pollString: String?): PollDto? {
        return pollString?.let { json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromReplyToDto(replyTo: ReplyToDto?): String? {
        return replyTo?.let { json.encodeToString(it) }
    }

    @TypeConverter
    fun toReplyToDto(replyToString: String?): ReplyToDto? {
        return replyToString?.let { json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromActorDto(actor: ActorDto): String {
        return json.encodeToString(actor)
    }

    @TypeConverter
    fun toActorDto(actorString: String): ActorDto {
        return json.decodeFromString(actorString)
    }

}
