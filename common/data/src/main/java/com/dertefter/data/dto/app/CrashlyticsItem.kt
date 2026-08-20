package com.dertefter.data.dto.app

import kotlinx.serialization.Serializable

@Serializable
data class CrashlyticsItem(
    val name: String,
    val path: String
)
