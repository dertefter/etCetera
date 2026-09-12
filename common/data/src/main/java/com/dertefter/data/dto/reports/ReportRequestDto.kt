package com.dertefter.data.dto.reports

import kotlinx.serialization.Serializable

@Serializable
data class ReportRequestDto(
    val targetType: String,
    val targetId: String,
    val reason: String,
    val description: String? = null
)
