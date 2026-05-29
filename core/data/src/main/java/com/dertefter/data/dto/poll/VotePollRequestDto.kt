package com.dertefter.data.dto.poll

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VotePollRequestDto(
    @SerialName("optionIds") val optionIds: List<String>
)