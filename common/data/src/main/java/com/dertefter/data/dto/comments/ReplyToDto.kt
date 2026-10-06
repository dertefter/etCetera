package com.dertefter.data.dto.comments

import com.dertefter.data.dto.common.ActiveNicknameDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReplyToDto(
    @SerialName("id") val id: String,
    @SerialName("username") val username: String,
    @SerialName("displayName") val displayName: String,
    val activeNickname: ActiveNicknameDto? = null
)
