package com.dertefter.data.dto.feed

import kotlinx.serialization.Serializable

@Serializable
data class AttachmentDto(
    val id: String,
    val type: String,
    val url: String? = null,
    val width: Int? = null,
    val height: Int? = null,
    val mimeType: String? = null,
    val filename: String? = null,
    val size: Long? = null
)