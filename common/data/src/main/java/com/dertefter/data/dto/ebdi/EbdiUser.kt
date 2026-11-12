package com.dertefter.data.dto.ebdi

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EbdiUser(
    val username: String = "",

    val avatar: String = "",

    @SerialName("user_id")
    val id: String,

    @SerialName("display_name")
    val displayName: String = ""
)
