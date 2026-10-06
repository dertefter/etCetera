package com.dertefter.data.dto.new_post

import kotlinx.serialization.Serializable

@Serializable
data class NewPollDto(
    val question: String,
    val options: List<NewPollOptionDto>,
    val multipleChoice: Boolean
)