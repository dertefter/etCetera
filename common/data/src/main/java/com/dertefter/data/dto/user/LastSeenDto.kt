package com.dertefter.data.dto.user

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("unit")
sealed class LastSeenDto {
    @Serializable
    @SerialName("just_now")
    data object JustNow : LastSeenDto()

    @Serializable
    @SerialName("minutes")
    data class Minutes(val value: Int) : LastSeenDto()

    @Serializable
    @SerialName("hours")
    data class Hours(val value: Int) : LastSeenDto()

    @Serializable
    @SerialName("recently")
    data object Recently : LastSeenDto()

    @Serializable
    @SerialName("this_week")
    data object ThisWeek : LastSeenDto()

    @Serializable
    @SerialName("this_month")
    data object ThisMonth : LastSeenDto()

    @Serializable
    @SerialName("long_ago")
    data object LongAgo : LastSeenDto()
}
