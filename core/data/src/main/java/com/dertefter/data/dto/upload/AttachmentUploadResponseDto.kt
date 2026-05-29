package com.dertefter.data.dto.upload

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AttachmentUploadResponseDto(
    @SerialName("id") val id: String,
    @SerialName("url") val url: String? = null,
    @SerialName("mimeType") val mimeType: String? = null,
    @SerialName("filename") val filename: String? = null,
    @SerialName("size") val size: Long? = null
)