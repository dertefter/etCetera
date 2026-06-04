package com.dertefter.navigation

import kotlinx.serialization.Serializable

@Serializable
data class AttachmentNavigationModel(
    val id: String,
    val type: String,
    val url: String?,
    val mimeType: String? = null
)