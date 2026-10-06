package com.dertefter.data.dto.upload

import kotlinx.serialization.Serializable

@Serializable
data class AttachmentUploadResponseDto(
    val id: String,
    val url: String? = null,
    val mimeType: String? = null,
    val filename: String? = null,
    val size: Long? = null
)