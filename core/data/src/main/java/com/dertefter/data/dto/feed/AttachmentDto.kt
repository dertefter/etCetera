package com.dertefter.data.dto.feed

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AttachmentDto(
    @SerialName("id") val id: String,
    @SerialName("type") val type: String, // "image", "video"
    @SerialName("url") val url: String? = null,
    @SerialName("width") val width: Int? = null,
    @SerialName("height") val height: Int? = null,
    @SerialName("mimeType") val mimeType: String? = null,
    @SerialName("filename") val filename: String? = null,
    @SerialName("size") val size: Long? = null // Размер файла лучше держать в Long
)