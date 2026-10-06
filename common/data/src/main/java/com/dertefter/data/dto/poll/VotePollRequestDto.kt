package com.dertefter.data.dto.poll

import kotlinx.serialization.Serializable

@Serializable
data class VotePollRequestDto(
    val optionIds: List<String>
)