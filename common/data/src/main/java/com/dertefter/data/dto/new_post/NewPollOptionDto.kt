package com.dertefter.data.dto.new_post

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NewPollOptionDto(
    @SerialName("text") val text: String
)