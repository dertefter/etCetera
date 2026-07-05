package com.dertefter.data.dto.new_post

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NewPollDto(
    @SerialName("question") val question: String,
    @SerialName("options") val options: List<NewPollOptionDto>,
    @SerialName("multipleChoice") val multipleChoice: Boolean
)